package payment;

import java.util.Map;
import java.util.HashMap;

public class PaymentMethod {
    private final PaymentType type;
    private final Map<String, String> details;

    public PaymentMethod(PaymentType type) {
        this.type = type;
        this.details = new HashMap<>();
    }

    public PaymentMethod(PaymentType type, Map<String, String> details) {
        this.type = type;
        this.details = new HashMap<>(details);
    }

    public PaymentType getType() { return type; }
    public Map<String, String> getDetails() { return new HashMap<>(details); }
    public String getDetail(String key) { return details.get(key); }

    // Factory methods for common payment methods
    public static PaymentMethod creditCard(String cardNumber, String expiryDate, String cvv, String holderName) {
        Map<String, String> details = new HashMap<>();
        details.put("cardNumber", maskCardNumber(cardNumber));
        details.put("expiryDate", expiryDate);
        details.put("cvv", "***"); // Never store actual CVV
        details.put("holderName", holderName);
        return new PaymentMethod(PaymentType.CREDIT_CARD, details);
    }

    public static PaymentMethod debitCard(String cardNumber, String expiryDate, String cvv, String holderName) {
        Map<String, String> details = new HashMap<>();
        details.put("cardNumber", maskCardNumber(cardNumber));
        details.put("expiryDate", expiryDate);
        details.put("cvv", "***"); // Never store actual CVV
        details.put("holderName", holderName);
        return new PaymentMethod(PaymentType.DEBIT_CARD, details);
    }

    public static PaymentMethod paypal(String email) {
        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        return new PaymentMethod(PaymentType.PAYPAL, details);
    }

    public static PaymentMethod applePay(String deviceId) {
        Map<String, String> details = new HashMap<>();
        details.put("deviceId", deviceId);
        return new PaymentMethod(PaymentType.APPLE_PAY, details);
    }

    public static PaymentMethod googlePay(String accountId) {
        Map<String, String> details = new HashMap<>();
        details.put("accountId", accountId);
        return new PaymentMethod(PaymentType.GOOGLE_PAY, details);
    }

    public static PaymentMethod bankTransfer(String accountNumber, String routingNumber, String bankName) {
        Map<String, String> details = new HashMap<>();
        details.put("accountNumber", maskAccountNumber(accountNumber));
        details.put("routingNumber", routingNumber);
        details.put("bankName", bankName);
        return new PaymentMethod(PaymentType.BANK_TRANSFER, details);
    }

    private static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleaned = cardNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cleaned.substring(cleaned.length() - 4);
    }

    private static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }

    @Override
    public String toString() {
        return String.format("PaymentMethod{type=%s, details=%s}", type, details);
    }

    public enum PaymentType {
        CREDIT_CARD("Credit Card"),
        DEBIT_CARD("Debit Card"),
        PAYPAL("PayPal"),
        APPLE_PAY("Apple Pay"),
        GOOGLE_PAY("Google Pay"),
        BANK_TRANSFER("Bank Transfer"),
        CRYPTOCURRENCY("Cryptocurrency"),
        GIFT_CARD("Gift Card"),
        LOYALTY_POINTS("Loyalty Points");

        private final String displayName;

        PaymentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }
}