package com.docomodigital.delorean.voucher.config;

import java.math.BigDecimal;

/**
 * Application constants.
 */
public final class Constants {

    public static final String TYPE_NOT_FOUND_ERROR = "TYPE_NOT_FOUND";
    public static final String VOUCHER_NOT_FOUND_ERROR = "VOUCHER_NOT_FOUND";
    public static final String TYPE_NOT_YET_AVAILABLE_ERROR = "TYPE_NOT_YET_AVAILABLE";
    public static final String TYPE_DISABLED_ERROR = "TYPE_DISABLED";
    public static final String WRONG_STATUS_ERROR = "WRONG_STATUS";
    public static final String TYPE_EXPIRED_ERROR = "TYPE_EXPIRED";
    public static final String EXISTING_TRANSACTION_ID_ERROR = "EXISTING_TRANSACTION_ID";
    public static final String WRONG_TRANSACTION_ID_ERROR = "WRONG_TRANSACTION_ID";
    public static final String TRANSACTIONAL_CONFLICT_ERROR = "TRANSACTIONAL_CONFLICT";
    public static final String CDR_ACCOUNTING_ERROR = "CDR_ACCOUNTING_ERROR";
    public static final String CDR_VALIDATION_ERROR = "CDR_VALIDATION_ERROR";
    public static final String NO_VOUCHER_AVAILABLE_ERROR = "NO_VOUCHER_AVAILABLE";

    public static final String UNAUTHORIZED_SHOP_NAME = "UNAUTHORIZED_SHOP";
    public static final String API_KEY_HEADER = "X-Api-Key";
    public static final String SIGNATURE_HEADER_NAME = "X-Signature";
    public static final String VOUCHER_TYPE_NOT_FOUND_MESSAGE = "Voucher Type %s not found";
    
	public static final int CDR_CLASS = 11;
	public static final int CDR_TYPE = 5;
	public static final int CDR_SERVICE_ID = 16;
	public static final int CDR_INFO_3 = 0;
	public static final int CDR_INFOR_2 = 0;
	public static final int CDR_INFO_1 = 1;
	public static final String CDR_SENDER_ID = "CHARGE";
	public static final BigDecimal CDR_P_FACTOR = new BigDecimal("10000");
	public static final BigDecimal CDR_CHARGING_AMOUNT = new BigDecimal(1);

    private Constants() {
    }
}
