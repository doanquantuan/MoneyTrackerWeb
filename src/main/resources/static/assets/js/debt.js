document.addEventListener("DOMContentLoaded", function () {
    $(function () {
        const debtModal = new bootstrap.Modal(document.getElementById('debtModal'));
        const repayModal = new bootstrap.Modal(document.getElementById('repayModal'));
        const deleteConfirmModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));

        let debtIdToDelete = null;
        let accountsList = [];
        let debtsList = [];

        // Format helpers
        function formatMoney(amount) {
            return new Intl.NumberFormat('en-US', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount) + ' VND';
        }

        function formatDate(dateStr) {
            if (!dateStr) return '';
            // Date is like YYYY-MM-DDTHH:MM:SS or YYYY-MM-DD
            const parts = dateStr.split('T')[0].split('-');
            if (parts.length === 3) {
                return `${parts[2]}/${parts[1]}/${parts[0]}`;
            }
            return dateStr;
        }

        function formatDateTime(dateStr) {
            if (!dateStr) return '';
            const parts = dateStr.split('T');
            const datePart = parts[0].split('-');
            if (datePart.length === 3) {
                const formattedDate = `${datePart[2]}/${datePart[1]}/${datePart[0]}`;
                if (parts[1]) {
                    const timePart = parts[1].split(':');
                    if (timePart.length >= 2) {
                        return `${timePart[0]}:${timePart[1]} ${formattedDate}`;
                    }
                }
                return formattedDate;
            }
            return dateStr;
        }

        // Load accounts list
        function loadAccounts() {
            $.ajax({
                url: '/api/accounts',
                type: 'GET',
                success: function (data) {
                    accountsList = [];
                    for (const key in data) {
                        if (data.hasOwnProperty(key)) {
                            accountsList = accountsList.concat(data[key]);
                        }
                    }

                    // Populate dropdowns
                    const $debtAcc = $('#debtAccount');
                    $debtAcc.empty().append('<option value="" disabled selected>-- Chọn tài khoản --</option>');
                    accountsList.forEach(function (acc) {
                        $debtAcc.append($('<option>', { value: acc.accountId, text: `${acc.accountName} (${formatMoney(acc.currentBalance)})` }));
                    });

                    const $repayAcc = $('#repayAccount');
                    $repayAcc.empty().append('<option value="" disabled selected>-- Chọn tài khoản --</option>');
                    accountsList.forEach(function (acc) {
                        $repayAcc.append($('<option>', { value: acc.accountId, text: `${acc.accountName} (${formatMoney(acc.currentBalance)})` }));
                    });
                }
            });
        }

        // Load debts list
        function loadDebts() {
            const $borrowList = $('#borrowList');
            const $lendList = $('#lendList');

            $borrowList.html('<tr><td colspan="8" class="text-center py-4 text-muted"><i class="fas fa-spinner fa-spin me-2"></i>Đang tải khoản đi vay...</td></tr>');
            $lendList.html('<tr><td colspan="8" class="text-center py-4 text-muted"><i class="fas fa-spinner fa-spin me-2"></i>Đang tải khoản cho vay...</td></tr>');

            $.ajax({
                url: '/api/debts',
                type: 'GET',
                success: function (list) {
                    debtsList = list;
                    $borrowList.empty();
                    $lendList.empty();

                    let borrowCount = 0;
                    let lendCount = 0;

                    let totalBorrowBal = 0;
                    let totalLendBal = 0;

                    list.forEach(function (d) {
                        const accName = d.account ? d.account.accountName : 'N/A';

                        // Calculate remaining balance by checking repayments
                        let paidSum = 0;
                        if (d.repayments && Array.isArray(d.repayments)) {
                            d.repayments.forEach(r => paidSum += (r.principalComponent || 0));
                        }
                        const remaining = Math.max(d.principalAmount - paidSum, 0);

                        let statusBadge = '';
                        if (d.status === 'COMPLETED') {
                            statusBadge = '<span class="badge bg-success-subtle text-success border border-success-subtle px-2 py-1">Đã tất toán</span>';
                        } else if (d.status === 'ACTIVE') {
                            statusBadge = '<span class="badge bg-primary-subtle text-primary border border-primary-subtle px-2 py-1">Đang hoạt động</span>';
                        } else if (d.status === 'OVERDUE') {
                            statusBadge = '<span class="badge bg-danger-subtle text-danger border border-danger-subtle px-2 py-1">Quá hạn</span>';
                        } else {
                            statusBadge = `<span class="badge bg-light text-muted px-2 py-1">${d.status}</span>`;
                        }

                        const ratePeriodLabel = d.interestRateType === 'YEAR' ? 'năm' : 'tháng';

                        const detailBtn = `
                            <button class="btn btn-sm btn-outline-info view-history-btn" 
                                    data-id="${d.debtId}"
                                    title="Chi tiết khoản nợ">
                                <i class="fas fa-info-circle me-1"></i> Chi tiết khoản nợ
                            </button>
                        `;

                        const deleteBtn = `
                            <button class="btn btn-sm btn-outline-danger delete-debt-btn" data-id="${d.debtId}">
                                <i class="fas fa-trash-alt"></i>
                            </button>
                        `;

                        const row = `
                            <tr>
                                <td class="ps-4"><strong>${d.partnerName}</strong></td>
                                <td>${accName}</td>
                                <td>${formatMoney(d.principalAmount)}</td>
                                <td>${d.interestRate}% / ${ratePeriodLabel} (${d.interestType === 'SIMPLE' ? 'Lãi đơn' : 'Lãi kép'})</td>
                                <td class="fw-bold text-dark">${formatMoney(remaining)}</td>
                                <td>${formatDate(d.dueDate)}</td>
                                <td>${statusBadge}</td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-2">
                                        ${detailBtn}
                                        ${deleteBtn}
                                    </div>
                                </td>
                            </tr>
                        `;

                        if (d.type === 'BORROW') {
                            $borrowList.append(row);
                            borrowCount++;
                            if (d.status !== 'COMPLETED' && d.status !== 'CANCELLED') {
                                totalBorrowBal += remaining;
                            }
                        } else {
                            $lendList.append(row);
                            lendCount++;
                            if (d.status !== 'COMPLETED' && d.status !== 'CANCELLED') {
                                totalLendBal += remaining;
                            }
                        }
                    });

                    // Update balances
                    $('#totalBorrowBalance').text(formatMoney(totalBorrowBal));
                    $('#totalLendBalance').text(formatMoney(totalLendBal));

                    if (borrowCount === 0) {
                        $borrowList.append('<tr><td colspan="8" class="text-center py-4 text-muted">Không có khoản nợ đi vay nào.</td></tr>');
                    }
                    if (lendCount === 0) {
                        $lendList.append('<tr><td colspan="8" class="text-center py-4 text-muted">Không có khoản cho vay nào.</td></tr>');
                    }
                },
                error: function () {
                    $borrowList.html('<tr><td colspan="8" class="text-center py-4 text-danger">Không thể tải dữ liệu.</td></tr>');
                    $lendList.html('<tr><td colspan="8" class="text-center py-4 text-danger">Không thể tải dữ liệu.</td></tr>');
                }
            });
        }

        // On debt type change, adjust the input labels in Modal
        $('#debtType').on('change', function () {
            const val = $(this).val();
            if (val === 'BORROW') {
                $('#accountLabel').text('Tài khoản nhận tiền *');
            } else {
                $('#accountLabel').text('Tài khoản chuyển tiền *');
            }
        });

        // Synchronize repaymentPeriod and dueDate reactive inputs
        function syncDatesAndPeriod() {
            const startVal = $('#startDate').val();
            if (!startVal) return;
            
            const start = new Date(startVal);
            
            // If due date changed, calculate months term
            $('#dueDate').off('change').on('change', function () {
                const dueVal = $(this).val();
                if (!dueVal) return;
                const due = new Date(dueVal);
                const diffTime = Math.max(due - start, 0);
                const diffDays = diffTime / (1000 * 60 * 60 * 24);
                const months = Math.max(Math.round(diffDays / 30.4375), 1);
                $('#repaymentPeriod').val(months);
            });

            // If repaymentPeriod changed, calculate due date
            $('#repaymentPeriod').off('input').on('input', function () {
                const months = parseInt($(this).val()) || 1;
                const start = new Date($('#startDate').val());
                start.setMonth(start.getMonth() + months);
                $('#dueDate').val(start.toISOString().split('T')[0]);
            });
        }

        // Open Add modal
        $('#addDebtBtn').on('click', function () {
            $('#modalErrorAlert').addClass('d-none').text('');
            document.getElementById('debtForm').reset();
            $('#debtType').trigger('change');

            // Set default dates
            const today = new Date().toISOString().split('T')[0];
            $('#startDate').val(today);
            $('#repaymentPeriod').val(1);

            const due = new Date();
            due.setMonth(due.getMonth() + 1);
            $('#dueDate').val(due.toISOString().split('T')[0]);

            syncDatesAndPeriod();

            // Setup date change sync too
            $('#startDate').on('change', function() {
                syncDatesAndPeriod();
                $('#repaymentPeriod').trigger('input');
            });

            loadAccounts();
            debtModal.show();
        });

        // Save new debt
        $('#saveDebtBtn').on('click', function () {
            const form = document.getElementById('debtForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            // Append start date & due date with times for LocalDateTime serialization
            const start = $('#startDate').val() + 'T00:00:00';
            const due = $('#dueDate').val() + 'T00:00:00';

            const debtData = {
                type: $('#debtType').val(),
                partnerName: $('#partnerName').val(),
                accountId: parseInt($('#debtAccount').val()),
                principalAmount: parseFloat($('#principalAmount').val()),
                interestRate: parseFloat($('#interestRate').val()) || 0.0,
                interestRateType: $('#interestRateType').val(),
                interestType: $('#interestType').val(),
                repaymentPeriod: parseInt($('#repaymentPeriod').val()) || 1,
                startDate: start,
                dueDate: due
            };

            const $btn = $(this);
            $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Đang lưu...');
            $('#modalErrorAlert').addClass('d-none').text('');

            $.ajax({
                url: '/api/debts',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(debtData),
                success: function () {
                    $btn.prop('disabled', false).text('Lưu');
                    debtModal.hide();
                    loadDebts();
                },
                error: function (xhr) {
                    $btn.prop('disabled', false).text('Lưu');
                    let errorMsg = 'Lỗi không thể lưu khoản nợ.';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMsg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        errorMsg = xhr.responseText;
                    }
                    $('#modalErrorAlert').removeClass('d-none').text(errorMsg);
                }
            });
        });

        // Open Repayment Modal
        $(document).on('click', '.repay-btn', function () {
            const id = $(this).data('id');
            const type = $(this).data('type');
            const partner = $(this).data('partner');
            const remaining = $(this).data('remaining');

            $('#repayDebtId').val(id);
            $('#repayDebtType').val(type);

            if (type === 'BORROW') {
                $('#repayModalLabel').text('Thanh toán khoản nợ (BORROW)');
                $('#repayAccountLabel').text('Tài khoản trích tiền chi trả *');
                $('#saveRepayBtn').text('Xác nhận trả nợ');
            } else {
                $('#repayModalLabel').text('Thu hồi khoản nợ (LEND)');
                $('#repayAccountLabel').text('Tài khoản nhận tiền thanh toán *');
                $('#saveRepayBtn').text('Xác nhận thu hồi');
            }

            $('#repayPartnerDisplay').val(`${partner} (Còn nợ: ${formatMoney(remaining)})`);
            $('#amountPaid').val('');
            $('#principalComponent').val('');
            $('#interestComponent').val('0');
            $('#repayNote').val('');
            $('#repayErrorAlert').addClass('d-none').text('');

            // Find the corresponding debt object from debtsList
            const d = debtsList.find(item => item.debtId == id);
            let accruedInterestRemaining = 0;
            if (d) {
                const p = d.principalAmount || 0;
                const r = d.interestRate || 0;
                const start = new Date(d.startDate);
                const today = new Date();
                const diffTime = Math.max(today - start, 0);
                const diffDays = diffTime / (1000 * 60 * 60 * 24);
                const diffMonths = diffDays / 30.4375;

                let totalInterestAccrued = 0;
                if (d.interestType === 'SIMPLE') {
                    totalInterestAccrued = p * (r / 100) * diffMonths;
                } else { // COMPOUND
                    totalInterestAccrued = p * (Math.pow(1 + (r / 100), diffMonths) - 1);
                }

                let interestPaid = 0;
                if (d.repayments && Array.isArray(d.repayments)) {
                    d.repayments.forEach(rep => interestPaid += (rep.interestComponent || 0));
                }

                accruedInterestRemaining = Math.max(totalInterestAccrued - interestPaid, 0);

                // Round values for display
                const displayAccrued = Math.round(accruedInterestRemaining);
                const displayRemaining = Math.round(remaining);

                // Display information on the UI
                $('#infoRemainingPrincipal').text(formatMoney(displayRemaining));
                $('#infoInterestRate').text(`${r}% / tháng (${d.interestType === 'SIMPLE' ? 'Lãi đơn' : 'Lãi kép'})`);
                $('#infoAccruedInterest').text(formatMoney(displayAccrued));
            } else {
                $('#infoRemainingPrincipal').text(formatMoney(remaining));
                $('#infoInterestRate').text('0% / tháng');
                $('#infoAccruedInterest').text('0 VND');
            }

            // Set default values in modal opening
            // Default principalComponent to remaining principal (full repayment)
            const defaultPrincipal = Math.round(remaining);
            $('#principalComponent').val(defaultPrincipal);
            $('#interestComponent').val(Math.round(accruedInterestRemaining));
            $('#amountPaid').val(Math.round(defaultPrincipal + accruedInterestRemaining));

            // Remove any old event listeners
            $('#principalComponent').off('input');

            // Autofill interestComponent and amountPaid when principalComponent changes
            $('#principalComponent').on('input', function () {
                let principal = parseFloat($(this).val());
                if (isNaN(principal)) {
                    $('#interestComponent').val(Math.round(accruedInterestRemaining));
                    $('#amountPaid').val(Math.round(accruedInterestRemaining));
                    return;
                }

                if (principal > remaining) {
                    principal = remaining;
                    $(this).val(Math.round(principal));
                }
                if (principal < 0) {
                    principal = 0;
                    $(this).val(0);
                }

                const interest = accruedInterestRemaining;
                const total = principal + interest;

                $('#interestComponent').val(Math.round(interest));
                $('#amountPaid').val(Math.round(total));
            });

            loadAccounts();
            repayModal.show();
        });

        // Confirm Repayment
        $('#saveRepayBtn').on('click', function () {
            const form = document.getElementById('repayForm');
            if (!form.checkValidity()) {
                form.reportValidity();
                return;
            }

            const id = $('#repayDebtId').val();
            const repayData = {
                amountPaid: parseFloat($('#amountPaid').val()),
                principalComponent: parseFloat($('#principalComponent').val()),
                interestComponent: parseFloat($('#interestComponent').val()) || 0.0,
                accountId: parseInt($('#repayAccount').val()),
                note: $('#repayNote').val()
            };

            const $btn = $(this);
            $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Đang xác nhận...');
            $('#repayErrorAlert').addClass('d-none').text('');

            $.ajax({
                url: `/api/debts/${id}/repayments`,
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(repayData),
                success: function () {
                    $btn.prop('disabled', false).text('Xác nhận');
                    repayModal.hide();
                    loadDebts();
                },
                error: function (xhr) {
                    $btn.prop('disabled', false).text('Xác nhận');
                    let errorMsg = 'Thực hiện thanh toán thất bại.';
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMsg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        errorMsg = xhr.responseText;
                    }
                    $('#repayErrorAlert').removeClass('d-none').text(errorMsg);
                }
            });
        });

        // Open Repayment History Page
        $(document).on('click', '.view-history-btn', function () {
            const id = $(this).data('id');
            window.location.href = `/debts/repayments?id=${id}`;
        });

        // Open delete confirm
        $(document).on('click', '.delete-debt-btn', function () {
            debtIdToDelete = $(this).data('id');
            deleteConfirmModal.show();
        });

        // Confirm delete
        $('#confirmDeleteBtn').on('click', function () {
            if (!debtIdToDelete) return;

            const $btn = $(this);
            $btn.prop('disabled', true).html('<i class="fas fa-spinner fa-spin"></i> Đang xóa...');

            $.ajax({
                url: '/api/debts/' + debtIdToDelete,
                type: 'DELETE',
                success: function () {
                    $btn.prop('disabled', false).text('Xóa');
                    deleteConfirmModal.hide();
                    loadDebts();
                },
                error: function (xhr) {
                    $btn.prop('disabled', false).text('Xóa');
                    let errorMsg = 'Lỗi không thể xóa khoản nợ.';
                    if (xhr.responseText) {
                        errorMsg = xhr.responseText;
                    }
                    alert(errorMsg);
                }
            });
        });

        // Initial Load
        loadDebts();
    });
});
