package com.hotelerp.userservice.constant;

public class ServiceConstant {

    // Task Management
    public static final String CREATE_TASK = "/createTask";
    public static final String GET_TASK_BY_ID = "/getTaskById/{id}";
    public static final String GET_ALL_TASKS = "/getAllTasks";
    public static final String GET_ACTIVE_TASKS = "/getActiveTasks";
    public static final String UPDATE_TASK = "/updateTask/{id}";
    public static final String DELETE_TASK = "/deleteTask/{id}";
    public static final String UPDATE_TASK_STATUS = "/updateTaskStatus/{id}";

    // Housekeeping Audit & SOP
    public static final String CREATE_CHECKPOINT = "/createCheckpoints";
    public static final String GET_ALL_CHECKPOINTS = "/getAllCheckpoints";
    public static final String GET_CHECKPOINTS_BY_FREQUENCY = "/getCheckpointsByFrequency/{frequency}";
    public static final String GET_COMMON_MASTER = "/getCommonMaster/{category}";
    public static final String SAVE_COMMON_MASTER = "/saveCommonMaster";
    public static final String UPDATE_COMMON_MASTER_DATA = "/updateCommonMasterData";
    public static final String GET_ROOM_LIVE_STATUS = "/rooms/{roomId}/live-status";

    // Lost & Found
    public static final String CREATE_LOST_FOUND_ITEM = "/createLostFoundItem";
    public static final String GET_LOST_ITEM_BY_ID = "/getLostItemById/{id}";
    public static final String GET_ALL_LOST_ITEMS = "/getAllLostItem";
    public static final String UPDATE_LOST_FOUND_ITEM = "/updateLostFoundItem/{id}";
    public static final String DELETE_LOST_FOUND_ITEM = "/deleteLostFoundItem/{id}";
    public static final String UPDATE_LOST_FOUND_ITEM_STATUS = "/updateLostFoundItemStatus/{id}";

    // Maintenance
    public static final String CREATE_MAINTENANCE = "/createMaintenance";
    public static final String GET_MAINTENANCE_BY_ID = "/getMaintenanceById/{id}";
    public static final String GET_ALL_MAINTENANCE = "/getAllMaintenance";
    public static final String GET_ACTIVE_MAINTENANCE = "/getActiveMaintenance";
    public static final String UPDATE_MAINTENANCE = "/updateMaintenance/{id}";
    public static final String DELETE_MAINTENANCE = "/deleteMaintenance/{id}";
    public static final String UPDATE_MAINTENANCE_STATUS = "/updateMaintenanceStatus/{id}";

    // Housekeeping Staff
    public static final String GET_HOUSEKEEPING_STAFF = "/getHousekeepingStaff";

    // POS Outlets
    public static final String CREATE_OUTLET = "/createOutlet";
    public static final String GET_OUTLET_BY_ID = "/getOutletById/{id}";
    public static final String GET_ALL_OUTLETS = "/getAllOutlets";
    public static final String UPDATE_OUTLET = "/updateOutlet/{id}";
    public static final String DELETE_OUTLET = "/deleteOutlet/{id}";

    // POS Tables
    public static final String CREATE_TABLE = "/createTable";
    public static final String GET_TABLE_BY_ID = "/getTableById/{id}";
    public static final String GET_ALL_TABLES = "/getAllTables";
    public static final String UPDATE_TABLE = "/updateTable/{id}";
    public static final String DELETE_TABLE = "/deleteTable/{id}";

    // POS Menu
    public static final String CREATE_MENU = "/createMenu";
    public static final String GET_MENU_BY_ID = "/getMenuById/{id}";
    public static final String GET_ALL_MENU = "/getAllMenu";
    public static final String UPDATE_MENU = "/updateMenu/{id}";
    public static final String DELETE_MENU = "/deleteMenu/{id}";

    // POS Orders
    public static final String CREATE_ORDER = "/createOrder";
    public static final String GET_ORDER_BY_ID = "/getOrderById/{id}";
    public static final String GET_ALL_ORDERS = "/getAllOrders";
    public static final String GET_ACTIVE_POS_ORDERS = "/getActiveOrders";
    public static final String UPDATE_ORDER = "/updateOrder/{id}";
    public static final String UPDATE_POS_ORDER_STATUS = "/updateOrderStatus/{id}";

    // Table Reservations
    public static final String BOOK_TABLE = "/bookTable";
    public static final String GET_TABLE_RESERVATIONS = "/getTableReservations/{tableId}";

    // Laundry Management
    public static final String CREATE_LAUNDRY_PRICE_MASTER = "/createPriceMaster";
    public static final String UPDATE_LAUNDRY_PRICE_MASTER = "/updatePriceMaster/{id}";
    public static final String GET_ALL_LAUNDRY_PRICE_MASTERS = "/getAllPriceMasters";
    public static final String GET_LAUNDRY_PRICE_MASTER_BY_ID = "/getPriceMasterById/{id}";
    public static final String DELETE_LAUNDRY_PRICE_MASTER = "/deletePriceMaster/{id}";

    public static final String CREATE_LAUNDRY_SERVICE_CATALOG = "/createServiceCatalog";
    public static final String UPDATE_LAUNDRY_SERVICE_CATALOG = "/updateServiceCatalog/{id}";
    public static final String GET_ALL_LAUNDRY_SERVICE_CATALOG = "/getAllServiceCatalog";
    public static final String GET_ACTIVE_LAUNDRY_SERVICE_CATALOG = "/getActiveServiceCatalog";
    public static final String GET_LAUNDRY_SERVICE_CATALOG_BY_ID = "/getServiceCatalogById/{id}";
    public static final String DELETE_LAUNDRY_SERVICE_CATALOG = "/deleteServiceCatalog/{id}";

    public static final String CREATE_LAUNDRY_ORDER = "/createOrder";
    public static final String UPDATE_LAUNDRY_ORDER = "/updateOrder/{id}";
    public static final String GET_ALL_LAUNDRY_ORDERS = "/getAllOrders";
    public static final String GET_LAUNDRY_ORDER_BY_ID = "/getOrderById/{id}";
    public static final String GET_NON_DELIVERED_LAUNDRY_ORDERS = "/getNonDeliveredOrders";
    public static final String UPDATE_LAUNDRY_ORDER_STATUS = "/updateOrderStatus/{id}";
    public static final String DELETE_LAUNDRY_ORDER = "/deleteOrder/{id}";

    private ServiceConstant() {
        // Prevent instantiation
    }
}
