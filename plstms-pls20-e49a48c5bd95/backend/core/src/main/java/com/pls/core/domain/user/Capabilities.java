package com.pls.core.domain.user;

/**
 * Constants for some of PLS2.0 capabilities. Normally, all these constants should be existed in
 * FLATBED.CAPABILITIES table. <br />
 * <b>Some notes about this class:</b>
 * 
 * <ul>
 * <li>This is only some of possible PLS2.0 related capabilities. Actually DB may have other values. This is
 * why this enum should not be used in Hibernate mapping.</li>
 * <li>Developers should synchronise this enum with actual DB records by manually.</li>
 * </ul>
 * 
 * @author Maxim Medvedev
 */
public enum Capabilities {

    /**
     * Work with Calendar.
     */
    ACCOUNT_HISTORY_CALENDAR_VIEW,

    /**
     * Work with Account History Search.
     */
    ACCOUNT_HISTORY_PAGE_VIEW,

    /**
     * Can create address with self-option.
     */
    CAN_CREATE_ADDRESSES_WITH_SELF_OPTION,

    /**
     * Can Add/edit address.
     */
    ADD_EDIT_ADDRESS_BOOK_PAGE,

    /**
     * Can Delete Address.
     */
    DELETE_ADDRESS,

    /**
     * Can Import Address.
     */
    IMPORT_ADDRESS,

    /**
     * Can View Address Only.
     */
    VIEW_ADDRESS_ONLY,

    /**
     * View Active Shipments with all costs.
     */
    VIEW_ACTIVE_SHIPMENTS_COST_DETAILS,

    /**
     * View Active Shipments.
     */
    VIEW_ACTIVE_SHIPMENTS,

    /**
     * Work with alerts.
     */
    BOARD_ALERT_PAGE_VIEW,

    /**
     * View All Shipments with all costs.
     */
    VIEW_ALL_SHIPMENTS_COST_DETAILS,

    /**
     * View All Shipments.
     */
    VIEW_ALL_SHIPMENTS,

    /**
     * Dispatch Orders.
     */
    BOARD_BOOKED_PAGE_VIEW,

    /**
     * Can Add Vendor Bill.
     */
    BOARD_CAN_ADD_VENDOR_BILL,

    /**
     * Edit Sales Order.
     */
    BOARD_CAN_EDIT_SALES_ORDER,

    /**
     * Can regenerate Shipment Label.
     */
    BOARD_CAN_REGENERATE_SHIP_LABEL,

    /**
     * View Sales Order.
     */
    BOARD_CAN_VIEW_SALES_ORDER,

    /**
     * View Waiting For Vendor Bills.
     */
    BOARD_DELIVERED_PAGE_VIEW,

    /**
     * View Open Shipments.
     */
    BOARD_OPEN_PAGE_VIEW,

    /**
     * Can generate manually the new version of BOL.
     */
    CAN_REGENERATE_BOL,

    /**
     * Update Customer profile.
     */
    CUSTOMER_PROFILE_VIEW,

    /**
     * View Activities.
     */
    DASHBOARD_ACTIVITIES_VIEW,

    /**
     * View Analysis.
     */
    DASHBOARD_ANALISYS_VIEW,

    /**
     * Enable export reports.
     */
    DASHBOARD_EXPORT_REPORTS,

    /**
     * View Summaries.
     */
    DASHBOARD_SUMMARIES_VIEW,

    /**
     * Add/Edit Adjustments.
     */
    CAN_ADD_EDIT_ADJ,

    /**
     * Approve Invoices.
     */
    CAN_APPROVE_INVOICE,

    /**
     * Export Invoices.
     */
    CAN_EXPORT_INVOICE,

    /**
     * Process Invoices.
     */
    CAN_PROCESS_INVOICE,

    /**
     * View Invoice Audit.
     */
    FIN_BOARD_AUDIT_PAGE_VIEW,

    /**
     * View Consolidated Invoices.
     */
    FIN_BOARD_CONSOLIDATED_PAGE_VIEW,

    /**
     * Cancel errors.
     */
    FIN_BOARD_ERRORS_CANCEL,

    /**
     * View Invoice Errors.
     */
    FIN_BOARD_ERRORS_VIEW,

    /**
     * View Invoice History.
     */
    FIN_BOARD_HISTORY_VIEW,

    /**
     * Send to Audit.
     */
    FIN_BOARD_SEND_TO_AUDIT,

    /**
     * View Transactional Invoices.
     */
    FIN_BOARD_TRANSACTIONAL_PAGE_VIEW,

    /**
     * Mark user as Account Executive.
     */
    ACCOUNT_EXECUTIVE,

    /**
     * Work with Credit And Billing.
     */
    INVOICES_CREDIT_BILLING_PAGE_VIEW,

    /**
     * Work with Invoices.
     */
    INVOICES_REPORTS_PAGE_VIEW,

    /**
     * Auto-Dispatch option for orders.
     */
    ALLOW_SHIPMENT_AUTO_DISPATCH,

    /**
     * Can cancel orders in any status.
     */
    CAN_CANCEL_ORDER,

    /**
     * View Hidden rates.
     */
    CAN_VIEW_HIDDEN_RATES,

    /**
     * Rate or Save Quote.
     */
    QUOTES_VIEW,
    
    /**
     * Can request VLTL rates/quotes
     */
    REQUEST_VLTL_RATES,

    /**
     * Require BOL # for orders.
     */
    REQUIRE_SHIPMENT_BOL,

    /**
     * Require GL # for orders.
     */
    REQUIRE_SHIPMENT_GL,

    /**
     * Require PO # for orders.
     */
    REQUIRE_SHIPMENT_PO,

    /**
     * Require PU # for orders.
     */
    REQUIRE_SHIPMENT_PU,

    /**
     * Require Shipper Ref# for orders.
     */
    REQUIRE_SHIPMENT_REF,

    /**
     * Require SO # for orders.
     */
    REQUIRE_SHIPMENT_SO,

    /**
     * Require Trailer # for orders.
     */
    REQUIRE_SHIPMENT_TRAILER,

    /**
     * Create a New Sales Order.
     */
    SALES_ORDER_VIEW,

    /**
     * Can create product with self-option.
     */
    PRODUCT_LIST_CREATE_SELF,

    /**
     * Work with pricing.
     */
    PRICING_PAGE_VIEW,

    /**
     * Add/Edit Role.
     */
    ADD_EDIT_ROLE,

    /**
     * Delete Role.
     */
    DELETE_ROLE,

    /**
     * Manage Users.
     */
    USERS_PAGE_VIEW,

    /**
     * View Unmatched Vendor Bills.
     */
    VENDOR_BILL_PAGE_VIEW,

    /**
     * View Archived Vendor Bills.
     */
    VEND_BILL_ARCHIVED_VIEW,

    /**
     * Can archive and move to unmatched.
     */
    VEND_BILL_ARCHIVE_AND_UNMATCH,

    /**
     * Can create Sales Order.
     */
    VEND_BILL_CREATE_SALES_ORDER,

    /**
     * Can Search Sales Order.
     */
    VEND_BILL_SEARCH_SALES_ORDER,

    /**
     * Can Add Shipment Entry.
     */
    ADD_SHIPMENT_ENTRY,

    /**
     * Manage Customer Service Contact Info.
     */
    MANAGE_CUSTOMER_SERVICE_CONTACT_INFO,

    /**
     * Edit from All Shipments when load is on Financial Board.
     */
    EDIT_FROM_ALL_SHIPMENTS_WHEN_LOAD_ON_FINANCIAL_BOARD,

    /**
     * Can Add Agent Business Unit.
     */
    ADD_CUSTOMER_FOR_AGENT_BUSINESS_UNIT,

    /**
     * Can Add Carrier Sales Business Unit.
     */
    ADD_CUSTOMER_FOR_CARRIER_SALES_BUSINESS_UNIT,

    /**
     * Can Add Ltl Business Unit.
     */
    ADD_CUSTOMER_FOR_LTL_BUSINESS_UNIT,

    /**
     * Can Add Pls Business Unit.
     */
    ADD_CUSTOMER_FOR_PLS_BUSINESS_UNIT,

    /**
     * Can Add Freight Solutions Business Unit.
     */
    ADD_CUSTOMER_FOR_PLS_FREIGHT_SOLUTIONS_BUSINESS_UNIT,

    /**
     * View Price Audit page.
     */
    VIEW_PRICE_AUDIT,

    /**
     * Can edit My Profile.
     */
    EDIT_MY_PROFILE,

    /**
     * Can Change Password.
     */
    CHANGE_PASSWORD,

    /**
     * View My Profile.
     */
    VIEW_MY_PROFILE,

    /**
     * Can override Date Hold for Waiting for Vendor Bill.
     */
    OVERRIDE_DATE_HOLD,

    /**
     * Do not display time for Shipment created by.
     */
    DO_NOT_DISPLAY_TIME_FOR_SHIPMENT_CREATED_BY,

    /**
     * View shipments revenue.
     */
    VIEW_ALL_SHIPMENTS_REVENUE_ONLY,

    /**
     * View active shipments revenue.
     */
    VIEW_ACTIVE_SHIPMENTS_REVENUE_ONLY,

    /**
     * View Active Customer Profile.
     */
    VIEW_ACTIVE_CUSTOMER_PROFILE,

    /**
     * Execute Unbilled Report.
     */
    EXECUTE_UNBILLED_REPORT,

    /**
     * View PLS customer cost.
     */
    VIEW_PLS_CUSTOMER_COST,

    /**
     * View PLS customer cost details.
     */
    VIEW_PLS_CUSTOMER_COST_DETAILS,

    /**
     * View PLS Revenue and Carrier cost.
     */
    VIEW_PLS_REVENUE_AND_CARRIER_COST,

    /**
     * Edit PLS Revenue.
     */
    EDIT_PLS_REVENUE,

    /**
     * Edit Carrier Cost.
     */
    EDIT_CARRIER_COST,
    
    /**
     * Override sales order carrier cost
     */
    OVERRIDE_SO_CARRIER_COST,

    /**
     * Can Add/edit product.
     */
    ADD_EDIT_PRODUCT,

    /**
     * Can Delete Product.
     */
    DELETE_PRODUCT,

    /**
     * Can Import Product.
     */
    IMPORT_PRODUCT,

    /**
     * Can View Products Only.
     */
    VIEW_PRODUCTS_ONLY,

    /**
     * Edit Shipment Before Pickup.
     */
    EDIT_SHIPMENT_BEFORE_PICKUP,

    /**
     * Require Phone Numbers for orders.
     */
    REQUIRE_PHONE_NUMBERS_FOR_ORDERS,

    /**
     * Edit Shipment After Pickup.
     */
    EDIT_SHIPMENT_AFTER_PICKUP,

    /**
     * Cancel Shipment Before Dispatched.
     */
    CANCEL_SHIPMENT_BEFORE_DISPATCHED,

    /**
     * Export Shipment List.
     */
    EXPORT_SHIPMENT_LIST,

    /**
     * Select EDI Invoice on Bill To.
     */
    SELECT_EDI_INVOICE_ON_BILL_TO,

    /**
     * Create Manual BOL.
     */
    CREATE_MANUAL_BOL,

    /**
     * Edit Manual BOL.
     */
    EDIT_MANUAL_BOL,

    /**
     * Cancel Manual BOL.
     */
    CANCEL_MANUAL_BOL,

    /**
     * View Manual BOL.
     */
    VIEW_MANUAL_BOL,

    /**
     * View B2Bi Logs.
     */
    ADMIN_LOG_VIEW,

    /**
     * View/Edit EDI settings.
     */
    VIEW_EDIT_EDI_SETTINGS,

    /**
     * View XML export button.
     */
    EXPORT_CONSOLIDATED_INVOICES_ORDER_LIST,

    /**
     * Execute Activity Report.
     */
    EXECUTE_ACTIVITY_REPORT,
    
    /**
     * Execute Carrier Activity Report.
     */
    EXECUTE_CARRIER_ACTIVITY_REPORT,

    /**
     * Execute Savings Report.
     */
    EXECUTE_SAVINGS_REPORT,

    /**
     * Execute Lost Savings Opportunity Report.
     */
    EXECUTE_LOST_SAVINGS_OPPORTUNITY_REPORT,

    /**
     * Execute Shipment Creation Report.
     */
    EXECUTE_SHIPMENT_CREATION_REPORT,

    /**
     * View Invoices Email History.
     */
    VIEW_INVOICES_EMAIL_HISTORY,

    /**
     * View Notifications Email History.
     */
    VIEW_NOTIFICATIONS_EMAIL_HISTORY,
    
    /**
     * View Confirmation Email History.
     */
    VIEW_CONFIRMATION_EMAIL_HISTORY,

    /**
     * View Documents Email History.
     */
    VIEW_DOCUMENTS_EMAIL_HISTORY,

    /**
     * View Pending Payment Email History.
     */
    VIEW_PEN_PAY_EMAIL_HISTORY,

    /**
     * Add Documents after Invoicing.
     */
    ADD_DOCUMENTS_AFTER_INVOICING,

    /**
     * Remove Documents after invoicing.
     */
    REMOVE_DOCUMENTS_AFTER_INVOICING,

    /**
     * Allow Re-Process to Financials.
     */
    ALLOW_REPROCESS_TO_FINANCIALS,

    /**
     * Allow edit Freight Bill Date.
     */
    EDIT_FREIGHT_BILL_DATE,

    /**
     * Export Shipment List with cost & margin.
     */
    EXPORT_SHIPMENT_LIST_WITH_COST_AND_MARGIN,

    /**
     * Allow generate Consignee Invoice.
     */
    CAN_GENERATE_CONSIGNEE_INVOICE,

    /**
     * Can Add/View Job #.
     */
    ADD_VIEW_JOB,

    /**
     * Export Products.
     */
    EXPORT_PRODUCTS,

    /**
     * Export Addresses.
     */
    EXPORT_ADDRESSES,

    /**
     * Set Customer Bill To Credit Limit.
     */
    SET_BILL_TO_CREDIT_LIMIT,

    /**
     * Require Cargo Value # for orders.
     */
    REQUIRE_SHIPMENT_CARGO,
    /**
     * Require Pro # for orders.
     */
    REQUIRE_SHIPMENT_PRO,

    /**
     * Require information that explains who requested the shipment.
     */
    REQUIRE_SHIPMENT_REQUESTED_BY,

    /**
     * Update Default Margin.
     */
    UPDATE_DEFAULT_MARGIN,

    /**
     * Update Carrier Block.
     */
    UPDATE_CARRIER_BLOCK,

    /**
     * Can Add freight Bill To on Shipment Entry and Quote.
     */
    CAN_ADD_FREIGHT_BILL_TO_ON_SHIPMENT_ENTRY_AND_QUOTES,

    /**
     * Can perform prices export.
     */
    CAN_EXPORT_PRICES,

    /**
     * Can perform prices import.
     */
    CAN_IMPORT_PRICES,

    /**
     * Can see Carrier details on Quotes and Shipment Entry.
     */
    VIEW_CARRIER_DETAILS,

    /**
     * Can add/edit Auditing Preferences on Bill To.
     */
    ADD_EDIT_AUDITING_PREFERENCES,

    /**
     * Can edit Audit tab for Dispute and Revenue Update.
     */
    CAN_EDIT_AUDIT_TAB_FOR_DISPUTE_AND_REVENUE_UPDATE,

    /**
     * Can export unbilled loads.
     */
    CAN_EXPORT_UNBILLED_LOADS,

    /**
     * Update Block Lane.
     */
    UPDATE_BLOCK_LANE,

    /**
     * Add quote without a customer.
     */
    ADD_QUOTE_WITHOUT_CUSTOMER,

    /**
     * Export Saved Quotes.
     */
    EXPORT_SAVED_QUOTES,

    /**
     * Can Add Customer for GOSHIP Business Unit to the User Mgt.
     */
    CAN_ADD_CUSTOMER_FOR_GOSHIP,

    /**
     * Can Edit GoShip Pricing.
     */
    CAN_EDIT_GOSHIP_PRICING,

    /**
     * Allows to search by Account Executive on All Shipments tab.
     */
    CAN_SEARCH_BY_ACC_EXECUTIVE,

    /**
     * Can view/edit/delete promo codes in User Mgt.
     */
    MANAGE_PROMO_CODES,

    /**
     * Can View Business Unit.
     */
    CAN_VIEW_BUSINESS_UNIT,

    /**
     * Can View Internal Notes.
     */
    CAN_VIEW_INTERNAL_NOTES,

    /**
     * Can manage announcements.
     */
    CAN_MANAGE_ANNOUNCEMENTS,

    /**
     * Don't send a dispatch to Carrier.
     */
    DO_NOT_SEND_DISPATCH_TO_CARRIER;
}
