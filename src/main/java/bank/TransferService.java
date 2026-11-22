package bank;

public class TransferService {

    private static final double TRANSFER_LIMIT = 50_000;

    private final CommissionPolicy commissionPolicy;
    private final NotificationService notificationService;
    private final BlockedAccountRegistry blockedAccountRegistry;

    public TransferService(CommissionPolicy commissionPolicy,
                           NotificationService notificationService) {
        this(commissionPolicy, notificationService, null);
    }

    public TransferService(CommissionPolicy commissionPolicy,
                           NotificationService notificationService,
                           BlockedAccountRegistry blockedAccountRegistry) {
        this.commissionPolicy = commissionPolicy;
        this.notificationService = notificationService;
        this.blockedAccountRegistry = blockedAccountRegistry;
    }

    public void transfer(BankAccount from, BankAccount to, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (from == to || from.getNumber().equals(to.getNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (amount > TRANSFER_LIMIT) {
            throw new TransferLimitExceededException("Transfer limit exceeded");
        }
        if (blockedAccountRegistry != null && blockedAccountRegistry.isBlocked(from.getNumber())) {
            throw new IllegalStateException("Source account is blocked");
        }

        double commission = commissionPolicy.calculate(amount);
        double totalWithdrawal = amount + commission;

        from.withdraw(totalWithdrawal);
        to.deposit(amount);
        notificationService.notify("Transfer " + amount + " completed");
    }
}
