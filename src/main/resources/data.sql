-- =============================================================
-- HMS Common Master Data Seed
-- Uses INSERT IGNORE to avoid duplicates on restart
-- =============================================================

-- -------------------------------------------------------
-- 1. ROOM STATUS  (Room.status -> CommonMaster)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('ROOM_STATUS', 'VACANT',      'Vacant',      'Room is currently vacant and available',         1, NOW(), NOW()),
('ROOM_STATUS', 'OCCUPIED',    'Occupied',    'Room is occupied by a guest',                    1, NOW(), NOW()),
('ROOM_STATUS', 'MAINTENANCE', 'Maintenance', 'Room is blocked for major maintenance',          1, NOW(), NOW()),
('ROOM_STATUS', 'RESERVED',    'Reserved',    'Room is reserved for an upcoming arrival',       1, NOW(), NOW()),
('ROOM_STATUS', 'CLEANING',    'Cleaning',    'Housekeeping is currently cleaning the room',    1, NOW(), NOW());

-- -------------------------------------------------------
-- 2. HK STATUS  (Room.hkStatus -> CommonMaster)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('HK_STATUS', 'VC',  'Vacant Clean',       'Room is vacant and cleaned — ready for new guest',           1, NOW(), NOW()),
('HK_STATUS', 'VD',  'Vacant Dirty',       'Room is vacant but requires cleaning',                       1, NOW(), NOW()),
('HK_STATUS', 'OC',  'Occupied Clean',     'Guest is in-house and the room has been cleaned',            1, NOW(), NOW()),
('HK_STATUS', 'OD',  'Occupied Dirty',     'Guest is in-house and room requires cleaning',               1, NOW(), NOW()),
('HK_STATUS', 'INS', 'Inspected',          'Room cleaned and verified by a supervisor',                  1, NOW(), NOW()),
('HK_STATUS', 'OOO', 'Out of Order',       'Room unavailable for sale due to major issues',              1, NOW(), NOW()),
('HK_STATUS', 'DND', 'Do Not Disturb',     'Guest has placed a DND request — do not enter',             1, NOW(), NOW()),
('HK_STATUS', 'UM',  'Under Maintenance',  'Room undergoing minor maintenance work',                     1, NOW(), NOW());

-- -------------------------------------------------------
-- 3. SOP FREQUENCY  (SOPCheckpoint.frequency)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('SOP_FREQUENCY', 'DAILY',   'Daily',   'Inspection carried out every day',    1, NOW(), NOW()),
('SOP_FREQUENCY', 'WEEKLY',  'Weekly',  'Inspection carried out every week',   1, NOW(), NOW()),
('SOP_FREQUENCY', 'MONTHLY', 'Monthly', 'Inspection carried out every month',  1, NOW(), NOW());

-- -------------------------------------------------------
-- 4. AUDIT AREA  (SOPCheckpoint.auditArea)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('AUDIT_AREA', 'BATHROOM',  'Bathroom',       'Bathroom and toilet inspection area',     1, NOW(), NOW()),
('AUDIT_AREA', 'BEDROOM',   'Bedroom',        'Bedroom and linen inspection area',       1, NOW(), NOW()),
('AUDIT_AREA', 'MINIBAR',   'Minibar',        'Minibar replenishment check',             1, NOW(), NOW()),
('AUDIT_AREA', 'CORRIDOR',  'Corridor',       'Hallway and corridor cleanliness',        1, NOW(), NOW()),
('AUDIT_AREA', 'BALCONY',   'Balcony',        'Balcony / terrace area inspection',       1, NOW(), NOW());

-- -------------------------------------------------------
-- 5. RESPONSIBLE ROLE  (SOPCheckpoint.responsibleRole)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('RESPONSIBLE_ROLE', 'ROOM_ATTENDANT', 'Room Attendant', 'Front-line HK staff responsible for daily cleaning', 1, NOW(), NOW()),
('RESPONSIBLE_ROLE', 'HK_SUPERVISOR',  'HK Supervisor',  'Supervisor who inspects and approves room status',   1, NOW(), NOW()),
('RESPONSIBLE_ROLE', 'FLOOR_MANAGER',  'Floor Manager',  'Floor-level manager overseeing HK operations',       1, NOW(), NOW());

-- -------------------------------------------------------
-- 6. POS ORDER STATUS  (PosOrder.status)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('POS_ORDER_STATUS', 'OPEN',       'Open',       'Order is open and taking items',           1, NOW(), NOW()),
('POS_ORDER_STATUS', 'KOT_SENT',   'KOT Sent',   'Kitchen order ticket sent to the kitchen', 1, NOW(), NOW()),
('POS_ORDER_STATUS', 'HELD',       'Held',       'Order is on hold temporarily',             1, NOW(), NOW()),
('POS_ORDER_STATUS', 'CLOSED',     'Closed',     'Order is closed and billed',               1, NOW(), NOW()),
('POS_ORDER_STATUS', 'CANCELLED',  'Cancelled',  'Order has been cancelled',                 1, NOW(), NOW());

-- -------------------------------------------------------
-- 7. POS ORDER TYPE  (PosOrder.orderType)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('POS_ORDER_TYPE', 'DINE_IN',      'Dine In',      'Customer dining at the restaurant table',  1, NOW(), NOW()),
('POS_ORDER_TYPE', 'ROOM_SERVICE', 'Room Service',  'Order delivered to guest room',             1, NOW(), NOW()),
('POS_ORDER_TYPE', 'TAKEAWAY',     'Takeaway',      'Order taken away by the customer',          1, NOW(), NOW());

-- -------------------------------------------------------
-- 8. BILL STATUS  (PosBill.status)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('BILL_STATUS', 'PENDING',  'Pending',  'Bill is generated but not yet settled',  1, NOW(), NOW()),
('BILL_STATUS', 'SETTLED',  'Settled',  'Bill has been fully settled/paid',        1, NOW(), NOW()),
('BILL_STATUS', 'VOID',     'Void',     'Bill has been voided/cancelled',          1, NOW(), NOW());

-- -------------------------------------------------------
-- 9. PAYMENT METHOD  (PosBill.paymentMethod)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('PAYMENT_METHOD', 'CASH',        'Cash',        'Payment made in cash',                       1, NOW(), NOW()),
('PAYMENT_METHOD', 'CARD',        'Card',        'Payment via credit or debit card',           1, NOW(), NOW()),
('PAYMENT_METHOD', 'UPI',         'UPI',         'Payment via UPI (GPAY, PhonePe, etc.)',      1, NOW(), NOW()),
('PAYMENT_METHOD', 'ROOM_CHARGE', 'Room Charge', 'Charge posted to the guest room account',    1, NOW(), NOW()),
('PAYMENT_METHOD', 'COMPLIMENT',  'Compliment',  'Complimentary — no charge',                  1, NOW(), NOW());

-- -------------------------------------------------------
-- 10. DINING TABLE STATUS  (DiningTable.status)
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('TABLE_STATUS', 'AVAILABLE', 'Available', 'Table is free and ready for seating',   1, NOW(), NOW()),
('TABLE_STATUS', 'OCCUPIED',  'Occupied',  'Table is currently occupied by guests',  1, NOW(), NOW()),
('TABLE_STATUS', 'RESERVED',  'Reserved',  'Table is reserved for a booking',        1, NOW(), NOW()),
('TABLE_STATUS', 'CLEANING',  'Cleaning',  'Table is being cleaned',                 1, NOW(), NOW());

-- -------------------------------------------------------
-- 11. OUTLET TYPES
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('OUTLET_TYPE', 'RESTAURANT', 'Restaurant', 'Full service dining restaurant', 1, NOW(), NOW()),
('OUTLET_TYPE', 'BAR',        'Bar',        'Bar and lounge outlet',           1, NOW(), NOW()),
('OUTLET_TYPE', 'CAFE',       'Cafe',       'Cafe and snacks outlet',          1, NOW(), NOW());

-- -------------------------------------------------------
-- 12. MENU CATEGORY
-- -------------------------------------------------------
INSERT IGNORE INTO common_masters (category, code, value, description, is_active, created_at, updated_at) VALUES
('MENU_CATEGORY', 'STARTERS',  'Starters',  'Appetizers and starters',  1, NOW(), NOW()),
('MENU_CATEGORY', 'MAIN',      'Main',      'Main course items',         1, NOW(), NOW()),
('MENU_CATEGORY', 'BEVERAGE',  'Beverage',  'Hot and cold beverages',    1, NOW(), NOW()),
('MENU_CATEGORY', 'DESSERT',   'Dessert',   'Desserts and sweets',       1, NOW(), NOW()),

-- -------------------------------------------------------
-- 13. RESERVATION STATUS
-- -------------------------------------------------------
('RESERVATION_STATUS', 'PENDING',   'Pending',   'Reservation is pending confirmation', 1, NOW(), NOW()),
('RESERVATION_STATUS', 'CONFIRMED', 'Confirmed', 'Reservation is confirmed',           1, NOW(), NOW()),
('RESERVATION_STATUS', 'CANCELLED', 'Cancelled', 'Reservation has been cancelled',    1, NOW(), NOW()),
('RESERVATION_STATUS', 'NOSHOW',    'No Show',    'Guest did not arrive',               1, NOW(), NOW()),

-- -------------------------------------------------------
-- 14. BOOKING STATUS
-- -------------------------------------------------------
('BOOKING_STATUS', 'CONFIRMED',   'Confirmed',   'Booking is confirmed',               1, NOW(), NOW()),
('BOOKING_STATUS', 'CHECKED_IN',  'Checked In',  'Guest has checked in',              1, NOW(), NOW()),
('BOOKING_STATUS', 'CHECKED_OUT', 'Checked Out', 'Guest has checked out',             1, NOW(), NOW()),
('BOOKING_STATUS', 'CANCELLED',   'Cancelled',   'Booking has been cancelled',         1, NOW(), NOW()),

-- -------------------------------------------------------
-- 15. MAINTENANCE STATUS
-- -------------------------------------------------------
('MAINTENANCE_STATUS', 'OPEN',        'Open',        'Maintenance request is open',           1, NOW(), NOW()),
('MAINTENANCE_STATUS', 'ASSIGNED',    'Assigned',    'Request assigned to a technician',      1, NOW(), NOW()),
('MAINTENANCE_STATUS', 'IN_PROGRESS', 'In Progress', 'Work is currently in progress',         1, NOW(), NOW()),
('MAINTENANCE_STATUS', 'ON_HOLD',     'On Hold',     'Work is temporarily on hold',           1, NOW(), NOW()),
('MAINTENANCE_STATUS', 'COMPLETED',   'Completed',   'Repair work has been completed',        1, NOW(), NOW()),
('MAINTENANCE_STATUS', 'CANCELLED',   'Cancelled',   'Request has been cancelled',            1, NOW(), NOW());

-- =============================================================
-- DEMO DATA FOR getDashboardData API
-- Financial Year: FY 2026-27 (April 2026 to March 2027)
-- =============================================================

-- -------------------------------------------------------
-- HOTEL
-- -------------------------------------------------------
INSERT IGNORE INTO hotels (id, name, email, phone, address, city, state, country, zip_code, total_rooms, currency, created_at, updated_at, is_active)
VALUES (1, 'Grand HMS Hotel', 'admin@grandhms.com', '+91-9800000001', '42, Park Street', 'Mumbai', 'Maharashtra', 'India', '400001', 20, 'INR', NOW(), NOW(), 1);

-- -------------------------------------------------------
-- FLOORS
-- -------------------------------------------------------
INSERT IGNORE INTO floor (id, hotel_id, floor_number, no_of_rooms, telephone, created_at, updated_at, is_active) VALUES
(1, 1, 'Floor 1', 5, '9001', NOW(), NOW(), 1),
(2, 1, 'Floor 2', 5, '9002', NOW(), NOW(), 1),
(3, 1, 'Floor 3', 5, '9003', NOW(), NOW(), 1),
(4, 1, 'Floor 4', 5, '9004', NOW(), NOW(), 1);

-- -------------------------------------------------------
-- ROOM TYPES
-- -------------------------------------------------------
INSERT IGNORE INTO room_types (id, hotel_id, name, capacity, base_price_per_night, area, description, is_active, created_at, updated_at) VALUES
(1, 1, 'Standard',  2, 3500.00,  250.00, 'Standard room with garden view',   1, NOW(), NOW()),
(2, 1, 'Deluxe',    2, 5500.00,  350.00, 'Deluxe room with city view',        1, NOW(), NOW()),
(3, 1, 'Suite',     4, 9500.00,  600.00, 'Luxury suite with sea view',        1, NOW(), NOW()),
(4, 1, 'Executive', 2, 7000.00,  450.00, 'Executive room with lounge access', 1, NOW(), NOW());

-- -------------------------------------------------------
-- ROOMS  (status_id references common_masters: OCCUPIED~2, VACANT~1)
-- -------------------------------------------------------
INSERT IGNORE INTO rooms (id, room_number, floor_id, type_id, status_id, hk_status_id, max_occupancy, telephone, created_at, updated_at, is_active, is_deleted) VALUES
-- Floor 1
(1,  '101', 1, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '1001', NOW(), NOW(), 1, 0),
(2,  '102', 1, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '1002', NOW(), NOW(), 1, 0),
(3,  '103', 1, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '1003', NOW(), NOW(), 1, 0),
(4,  '104', 1, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '1004', NOW(), NOW(), 1, 0),
(5,  '105', 1, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VD' LIMIT 1), 2, '1005', NOW(), NOW(), 1, 0),
-- Floor 2
(6,  '201', 2, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '2001', NOW(), NOW(), 1, 0),
(7,  '202', 2, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '2002', NOW(), NOW(), 1, 0),
(8,  '203', 2, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='MAINTENANCE' LIMIT 1),(SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OOO' LIMIT 1), 2, '2003', NOW(), NOW(), 1, 0),
(9,  '204', 2, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '2004', NOW(), NOW(), 1, 0),
(10, '205', 2, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='RESERVED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='INS' LIMIT 1), 2, '2005', NOW(), NOW(), 1, 0),
-- Floor 3
(11, '301', 3, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '3001', NOW(), NOW(), 1, 0),
(12, '302', 3, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '3002', NOW(), NOW(), 1, 0),
(13, '303', 3, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '3003', NOW(), NOW(), 1, 0),
(14, '304', 3, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='DND' LIMIT 1), 4, '3004', NOW(), NOW(), 1, 0),
(15, '305', 3, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '3005', NOW(), NOW(), 1, 0),
-- Floor 4
(16, '401', 4, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '4001', NOW(), NOW(), 1, 0),
(17, '402', 4, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '4002', NOW(), NOW(), 1, 0),
(18, '403', 4, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='INS' LIMIT 1), 2, '4003', NOW(), NOW(), 1, 0),
(19, '404', 4, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '4004', NOW(), NOW(), 1, 0),
(20, '405', 4, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='MAINTENANCE' LIMIT 1),(SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='UM' LIMIT 1), 4, '4005', NOW(), NOW(), 1, 0);

-- -------------------------------------------------------
-- RATE PLANS
-- -------------------------------------------------------
INSERT IGNORE INTO rate_plan_master (id, name, description, price_adjustment, display_order, is_active, created_at, updated_at) VALUES
(1, 'Room Only', 'Standard rate without meals', 0.00, 1, 1, NOW(), NOW()),
(2, 'Bed & Breakfast', 'Includes daily breakfast', 500.00, 2, 1, NOW(), NOW()),
(3, 'Half Board', 'Breakfast and Lunch/Dinner', 1200.00, 3, 1, NOW(), NOW());

-- -------------------------------------------------------
-- GUESTS
-- -------------------------------------------------------
INSERT IGNORE INTO guests (id, title, first_name, last_name, country_code, phone, email, city, country, nationality, is_active, is_deleted, created_at, updated_at) VALUES
(1, 'MR', 'Arun', 'Sharma', '+91', '9800000011', 'arun@example.com', 'Mumbai', 'India', 'Indian', 1, 0, NOW(), NOW()),
(2, 'MS', 'Priya', 'Nair', '+91', '9800000012', 'priya@example.com', 'Kochi', 'India', 'Indian', 1, 0, NOW(), NOW()),
(3, 'MR', 'John', 'Doe', '+1', '5550101', 'john.doe@example.com', 'New York', 'USA', 'American', 1, 0, NOW(), NOW());

-- -------------------------------------------------------
-- RESERVATIONS & BOOKINGS (FY 2026-27: April 2026 – March 2027)
-- -------------------------------------------------------
-- Reservation 1
INSERT IGNORE INTO reservations (id, guest_id, check_in_date, check_out_date, number_of_nights, number_of_adults, number_of_rooms, reservation_status_id, rate_plan_id, created_at, updated_at) VALUES
(1, 1, '2026-04-03', '2026-04-06', 3, 2, 1, (SELECT id FROM common_masters WHERE category='RESERVATION_STATUS' AND code='CONFIRMED' LIMIT 1), 1, '2026-04-01 10:00:00', NOW());
INSERT IGNORE INTO bookings (id, reservation_id, room_id, check_in_date, check_out_date, number_of_nights, rate_per_night, total_price, final_price, booking_status_id, created_at, updated_at) VALUES
(1, 1, 1, '2026-04-03', '2026-04-06', 3, 3500.00, 10500.00, 10500.00, (SELECT id FROM common_masters WHERE category='BOOKING_STATUS' AND code='CHECKED_OUT' LIMIT 1), '2026-04-01 10:00:00', NOW());

-- Reservation 2 (May)
INSERT IGNORE INTO reservations (id, guest_id, check_in_date, check_out_date, number_of_nights, number_of_adults, number_of_rooms, reservation_status_id, rate_plan_id, created_at, updated_at) VALUES
(2, 2, '2026-05-10', '2026-05-15', 5, 2, 1, (SELECT id FROM common_masters WHERE category='RESERVATION_STATUS' AND code='CONFIRMED' LIMIT 1), 2, '2026-05-01 11:00:00', NOW());
INSERT IGNORE INTO bookings (id, reservation_id, room_id, check_in_date, check_out_date, number_of_nights, rate_per_night, total_price, final_price, booking_status_id, created_at, updated_at) VALUES
(2, 2, 4, '2026-05-10', '2026-05-15', 5, 9500.00, 47500.00, 47500.00, (SELECT id FROM common_masters WHERE category='BOOKING_STATUS' AND code='CHECKED_OUT' LIMIT 1), '2026-05-01 11:00:00', NOW());

-- Reservation 3 (June)
INSERT IGNORE INTO reservations (id, guest_id, check_in_date, check_out_date, number_of_nights, number_of_adults, number_of_rooms, reservation_status_id, rate_plan_id, created_at, updated_at) VALUES
(3, 3, '2026-06-15', '2026-06-20', 5, 2, 1, (SELECT id FROM common_masters WHERE category='RESERVATION_STATUS' AND code='CONFIRMED' LIMIT 1), 1, '2026-06-01 09:00:00', NOW());
INSERT IGNORE INTO bookings (id, reservation_id, room_id, check_in_date, check_out_date, number_of_nights, rate_per_night, total_price, final_price, booking_status_id, created_at, updated_at) VALUES
(3, 3, 9, '2026-06-15', '2026-06-20', 5, 9500.00, 47500.00, 47500.00, (SELECT id FROM common_masters WHERE category='BOOKING_STATUS' AND code='CHECKED_OUT' LIMIT 1), '2026-06-01 09:00:00', NOW());

-- Reservation 4 (July)
INSERT IGNORE INTO reservations (id, guest_id, check_in_date, check_out_date, number_of_nights, number_of_adults, number_of_rooms, reservation_status_id, rate_plan_id, created_at, updated_at) VALUES
(4, 1, '2026-07-20', '2026-07-25', 5, 2, 1, (SELECT id FROM common_masters WHERE category='RESERVATION_STATUS' AND code='CONFIRMED' LIMIT 1), 1, '2026-07-01 10:00:00', NOW());
INSERT IGNORE INTO bookings (id, reservation_id, room_id, check_in_date, check_out_date, number_of_nights, rate_per_night, total_price, final_price, booking_status_id, created_at, updated_at) VALUES
(4, 4, 4, '2026-07-20', '2026-07-25', 5, 9500.00, 47500.00, 47500.00, (SELECT id FROM common_masters WHERE category='BOOKING_STATUS' AND code='CHECKED_OUT' LIMIT 1), '2026-07-01 10:00:00', NOW());



-- -------------------------------------------------------
-- OUTLETS
-- -------------------------------------------------------
INSERT IGNORE INTO outlets (id, name, type_id, location, timing, tax_profile, is_active, is_deleted, created_at, updated_at) VALUES
(1, 'The Grand Restaurant', (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='RESTAURANT' LIMIT 1), 'Ground Floor', '07:00-23:00', 'GST_5', 1, 0, NOW(), NOW()),
(2, 'Skybar Lounge',        (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='BAR'        LIMIT 1), 'Terrace',       '12:00-02:00', 'GST_18',1, 0, NOW(), NOW()),
(3, 'Morning Cafe',         (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='CAFE'       LIMIT 1), 'Lobby',         '06:00-12:00', 'GST_5', 1, 0, NOW(), NOW());

-- -------------------------------------------------------
-- MENU ITEMS
-- -------------------------------------------------------
INSERT IGNORE INTO menu_items (id, outlet_id, item_name, category_id, price, tax_percent, is_available, is_featured, is_deleted, created_at, updated_at) VALUES
-- Restaurant
(1,  1, 'Paneer Tikka',      (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 350.00, 5.00, 1, 1, 0, NOW(), NOW()),
(2,  1, 'Veg Biryani',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 450.00, 5.00, 1, 1, 0, NOW(), NOW()),
(3,  1, 'Butter Chicken',    (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 520.00, 5.00, 1, 0, 0, NOW(), NOW()),
(4,  1, 'Dal Makhani',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 380.00, 5.00, 1, 0, 0, NOW(), NOW()),
(5,  1, 'Gulab Jamun',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='DESSERT'  LIMIT 1), 180.00, 5.00, 1, 0, 0, NOW(), NOW()),
(6,  1, 'Masala Chai',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1),  80.00, 5.00, 1, 0, 0, NOW(), NOW()),
-- Skybar
(7,  2, 'Tropical Punch',    (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 450.00, 18.00, 1, 1, 0, NOW(), NOW()),
(8,  2, 'Beer Pint',         (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 380.00, 18.00, 1, 0, 0, NOW(), NOW()),
(9,  2, 'Nachos with Dip',   (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 290.00, 18.00, 1, 0, 0, NOW(), NOW()),
(10, 2, 'Grilled Chicken',   (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 650.00, 18.00, 1, 1, 0, NOW(), NOW()),
-- Cafe
(11, 3, 'Espresso',          (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 120.00, 5.00, 1, 1, 0, NOW(), NOW()),
(12, 3, 'Croissant',         (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 150.00, 5.00, 1, 1, 0, NOW(), NOW()),
(13, 3, 'Cold Coffee',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 180.00, 5.00, 1, 0, 0, NOW(), NOW()),
(14, 3, 'Waffles',           (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='DESSERT'  LIMIT 1), 220.00, 5.00, 1, 0, 0, NOW(), NOW());

-- -------------------------------------------------------
-- POS ORDERS & ITEMS  (FY 2026-27, spread across months)
-- -------------------------------------------------------
INSERT IGNORE INTO pos_orders (id, outlet_id, status_id, total_amount, is_deleted, created_at, updated_at) VALUES
(1,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1580.00, 0, '2026-04-05 13:30:00', '2026-04-05 14:00:00'),
(2,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1240.00, 0, '2026-05-10 20:00:00', '2026-05-10 20:45:00'),
(3,  2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2160.00, 0, '2026-06-15 21:00:00', '2026-06-15 21:30:00'),
(4,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1830.00, 0, '2026-07-20 13:00:00', '2026-07-20 13:45:00'),
(5,  3, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1),  450.00, 0, '2026-08-08 09:00:00', '2026-08-08 09:20:00'),
(6,  2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 3120.00, 0, '2026-09-22 22:00:00', '2026-09-22 22:45:00'),
(7,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2050.00, 0, '2026-10-12 14:00:00', '2026-10-12 14:30:00'),
(8,  3, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1),  620.00, 0, '2026-11-18 08:30:00', '2026-11-18 09:00:00'),
(9,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2890.00, 0, '2026-12-24 20:00:00', '2026-12-24 21:00:00'),
(10, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 4200.00, 0, '2027-01-01 23:00:00', '2027-01-01 23:45:00'),
(11,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1760.00, 0, '2027-02-14 20:00:00', '2027-02-14 20:45:00'),
(12, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2980.00, 0, '2027-03-08 21:00:00', '2027-03-08 22:00:00'),
-- Active open orders (for KOT queue)
(13, 1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='OPEN'   LIMIT 1), 0.00, 0, NOW(),                 NOW()),
(14, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='KOT_SENT' LIMIT 1), 0.00, 0, NOW(),                 NOW());

-- POS Order Items
INSERT IGNORE INTO pos_order_items (order_id, menu_item_id, quantity, price, subtotal) VALUES
-- Order 1
(1,  1, 2, 350.00, 700.00),
(1,  2, 1, 450.00, 450.00),
(1,  6, 3, 80.00,  240.00),
(1,  5, 1, 180.00, 180.00),
-- Order 2
(2,  3, 1, 520.00, 520.00),
(2,  4, 2, 380.00, 760.00),
-- Order 3
(3,  7, 2, 450.00, 900.00),
(3,  10,2, 650.00, 1300.00),
-- Order 4
(4,  2, 2, 450.00, 900.00),
(4,  1, 1, 350.00, 350.00),
(4,  3, 1, 520.00, 520.00),
-- Order 5
(5,  11,2, 120.00, 240.00),
(5,  12,1, 150.00, 150.00),
-- Order 6
(6,  7, 3, 450.00, 1350.00),
(6,  8, 3, 380.00, 1140.00),
(6,  9, 1, 290.00, 290.00),
-- Order 7
(7,  1, 3, 350.00, 1050.00),
(7,  3, 1, 520.00, 520.00),
(7,  5, 2, 180.00, 360.00),
-- Order 8
(8,  13,2, 180.00, 360.00),
(8,  14,1, 220.00, 220.00),
-- Order 9
(9,  2, 3, 450.00, 1350.00),
(9,  3, 2, 520.00, 1040.00),
(9,  5, 2, 180.00, 360.00),
-- Order 10
(10, 7, 4, 450.00, 1800.00),
(10, 8, 3, 380.00, 1140.00),
(10, 10,2, 650.00, 1300.00),
-- Order 11
(11, 4, 2, 380.00, 760.00),
(11, 2, 2, 450.00, 900.00),
-- Order 12
(12, 7, 3, 450.00, 1350.00),
(12, 10,2, 650.00, 1300.00),
-- Open orders
(13, 1, 2, 350.00, 700.00),
(13, 2, 1, 450.00, 450.00),
(14, 7, 2, 450.00, 900.00);

-- -------------------------------------------------------
-- 13. DEPARTMENTS (New Table)
-- -------------------------------------------------------
INSERT IGNORE INTO departments (id, name, description, is_active, is_deleted, created_at, updated_at) VALUES
(1, 'Housekeeping', 'Room cleaning and maintenance', 1, 0, NOW(), NOW()),
(2, 'F&B Service',  'Food and beverage service',      1, 0, NOW(), NOW()),
(3, 'Maintenance',   'Engineering and repairs',       1, 0, NOW(), NOW());

-- -------------------------------------------------------
-- 14. ROLES (New Table)
-- -------------------------------------------------------
INSERT IGNORE INTO roles (id, name, department_id, access_level, status, description, is_deleted) VALUES
(1, 'Property Admin', 1, 'Admin',      'Active', 'Full system access', 0),
(2, 'HK Supervisor',  1, 'Supervisor', 'Active', 'Managing HK operations', 0),
(3, 'Room Attendant', 1, 'Department', 'Active', 'Cleaning rooms', 0),
(4, 'Server',         2, 'Department', 'Active', 'Serving food in outlets', 0);

-- -------------------------------------------------------
-- USERS (Staff) - Linked to departments, roles, and hotels
-- -------------------------------------------------------
INSERT IGNORE INTO users (id, employee_id, full_name, username, email, phone, department_id, role_id, property_id, status, is_deleted, created_at, updated_at) VALUES
(1, 'EMP-101', 'Admin User', 'admin', 'admin@hms.com', '9800010001', 1, 1, 1, 'ACTIVE', 0, NOW(), NOW()),
(2, 'EMP-102', 'Sarah Wilson', 'sarah', 'sarah@hms.com', '9800010002', 1, 2, 1, 'ACTIVE', 0, NOW(), NOW()),
(3, 'EMP-103', 'John Doe', 'john', 'john@hms.com', '9800010003', 1, 3, 1, 'ACTIVE', 0, NOW(), NOW()),
(4, 'EMP-104', 'Mike Ross', 'mike', 'mike@hms.com', '9800010004', 2, 4, 1, 'ACTIVE', 0, NOW(), NOW());

-- -------------------------------------------------------
-- DINING TABLES
-- -------------------------------------------------------
INSERT IGNORE INTO dining_tables (id, outlet_id, table_number, status_id, covers, is_deleted, created_at, updated_at) VALUES
(1, 1, 'T-01', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='OCCUPIED' LIMIT 1), 4, 0, NOW(), NOW()),
(2, 1, 'T-02', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='AVAILABLE' LIMIT 1), 2, 0, NOW(), NOW()),
(3, 1, 'T-03', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='RESERVED'  LIMIT 1), 4, 0, NOW(), NOW()),
(4, 2, 'B-01', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='OCCUPIED' LIMIT 1), 2, 0, NOW(), NOW()),
(5, 2, 'B-02', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='CLEANING' LIMIT 1), 2, 0, NOW(), NOW());

-- -------------------------------------------------------
-- POS BILLS (for Revenue metrics)
-- -------------------------------------------------------
INSERT IGNORE INTO pos_bills (id, order_id, payment_method_id, bill_number, amount, status_id, created_at) VALUES
(1,  1,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CASH' LIMIT 1), 'BILL-001', 1580.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-04-05 14:00:00'),
(2,  2,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CARD' LIMIT 1), 'BILL-002', 1240.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-05-10 20:45:00'),
(3,  3,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='UPI'  LIMIT 1), 'BILL-003', 2160.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-06-15 21:30:00'),
(4,  4,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='ROOM_CHARGE' LIMIT 1), 'BILL-004', 1830.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-07-20 13:45:00'),
(5,  5,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CASH' LIMIT 1), 'BILL-005',  450.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-08-08 09:20:00'),
(6,  6,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CARD' LIMIT 1), 'BILL-006', 3120.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-09-22 22:45:00'),
(7,  7,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='UPI'  LIMIT 1), 'BILL-007', 2050.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-10-12 14:30:00'),
(8,  8,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CASH' LIMIT 1), 'BILL-008',  620.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-11-18 09:00:00'),
(9,  9,  (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CARD' LIMIT 1), 'BILL-009', 2890.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2026-12-24 21:00:00'),
(10, 10, (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='UPI'  LIMIT 1), 'BILL-010', 4200.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2027-01-01 23:45:00'),
(11, 11, (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='ROOM_CHARGE' LIMIT 1), 'BILL-011', 1760.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2027-02-14 20:45:00'),
(12, 12, (SELECT id FROM common_masters WHERE category='PAYMENT_METHOD' AND code='CASH' LIMIT 1), 'BILL-012', 2980.00, (SELECT id FROM common_masters WHERE category='BILL_STATUS' AND code='SETTLED' LIMIT 1), '2027-03-08 22:00:00');

-- -------------------------------------------------------
-- HOUSEKEEPING TASKS
-- -------------------------------------------------------
INSERT IGNORE INTO tasks (id, room_id, task_type, priority, assigned_user_id, status, is_deleted, created_at, updated_at) VALUES
(1, 2, 'Cleaning', 'MEDIUM', 3, 'PENDING', 0,     NOW(), NOW()),
(2, 5, 'Cleaning', 'HIGH',   3, 'IN_PROGRESS', 0, NOW(), NOW()),
(3, 7, 'Cleaning', 'LOW',    3, 'COMPLETED', 0,   NOW(), NOW()),
(4, 11,'Cleaning', 'HIGH',   3, 'PENDING', 0,     NOW(), NOW());

-- -------------------------------------------------------
-- MAINTENANCE REQUESTS
-- -------------------------------------------------------
INSERT IGNORE INTO maintenance_requests (id, room_id, repair_issue, category_id, priority_id, reported_by_id, status_id, is_deleted, reported_at, updated_at) VALUES
(1, 8, 'AC leaking water', 
    (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='MAINT' LIMIT 1),
    (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 3, 
    (SELECT id FROM common_masters WHERE category='MAINTENANCE_STATUS' AND code='IN_PROGRESS' LIMIT 1), 0, NOW(), NOW()),
(2, 20,'TV remote not working', 
    (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='MAINT' LIMIT 1),
    (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 3, 
    (SELECT id FROM common_masters WHERE category='MAINTENANCE_STATUS' AND code='OPEN' LIMIT 1), 0, NOW(), NOW());

-- -------------------------------------------------------
-- LOST AND FOUND ITEMS
-- -------------------------------------------------------
INSERT IGNORE INTO lost_found_items (id, room_id, item_description, category_id, found_by_id, storage_location, found_date, status, is_deleted, created_at, updated_at) VALUES
(1, 1, 'iPhone 13 Charger', (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='HK' LIMIT 1), 3, 'HK Locker A-12', '2026-06-10', 'STORED', 0, NOW(), NOW()),
(2, 9, 'Blue Wallet',       (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='HK' LIMIT 1), 3, 'Safe Box',        '2026-06-11', 'STORED', 0, NOW(), NOW());

-- -------------------------------------------------------
-- USER ROOM MAP (Assignments)
-- -------------------------------------------------------
INSERT IGNORE INTO user_room_map (id, user_id, room_id, assigned_at, assigned_by) VALUES
(1, 3, 1, NOW(), 'Admin'), (2, 3, 2, NOW(), 'Admin'), (3, 3, 3, NOW(), 'Admin'),
(4, 3, 4, NOW(), 'Admin'), (5, 3, 5, NOW(), 'Admin');

-- -------------------------------------------------------
-- SOP CHECKPOINTS
-- -------------------------------------------------------
INSERT IGNORE INTO sop_checkpoints (id, checkpoint_id, frequency_id, audit_area, responsible_role_id, description, created_at, updated_at) VALUES
(1, 'D01', (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 'BATHROOM',
    (SELECT id FROM common_masters WHERE category='RESPONSIBLE_ROLE' AND code='ROOM_ATTENDANT' LIMIT 1), 'Clean mirror and floor', NOW(), NOW()),
(2, 'D02', (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 'BEDROOM',
    (SELECT id FROM common_masters WHERE category='RESPONSIBLE_ROLE' AND code='ROOM_ATTENDANT' LIMIT 1), 'Check bedsheet stains', NOW(), NOW());

-- -------------------------------------------------------
-- ROOM AUDIT LOGS
-- -------------------------------------------------------
INSERT IGNORE INTO room_audit_logs (id, room_id, checkpoint_id, status, inspector_id, score, remarks, audit_date, created_at) VALUES
(1, 1, 1, 'PASS', 2, 100, 'All clean', NOW(), NOW()),
(2, 1, 2, 'FAIL', 2, 0, 'Stain found', NOW(), NOW());
