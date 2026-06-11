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
('MENU_CATEGORY', 'DESSERT',   'Dessert',   'Desserts and sweets',       1, NOW(), NOW());

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
INSERT IGNORE INTO rooms (id, room_number, floor_id, type_id, status_id, hk_status_id, max_occupancy, telephone, created_at, updated_at, is_active) VALUES
-- Floor 1
(1,  '101', 1, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '1001', NOW(), NOW(), 1),
(2,  '102', 1, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '1002', NOW(), NOW(), 1),
(3,  '103', 1, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '1003', NOW(), NOW(), 1),
(4,  '104', 1, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '1004', NOW(), NOW(), 1),
(5,  '105', 1, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VD' LIMIT 1), 2, '1005', NOW(), NOW(), 1),
-- Floor 2
(6,  '201', 2, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '2001', NOW(), NOW(), 1),
(7,  '202', 2, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '2002', NOW(), NOW(), 1),
(8,  '203', 2, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='MAINTENANCE' LIMIT 1),(SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OOO' LIMIT 1), 2, '2003', NOW(), NOW(), 1),
(9,  '204', 2, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '2004', NOW(), NOW(), 1),
(10, '205', 2, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='RESERVED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='INS' LIMIT 1), 2, '2005', NOW(), NOW(), 1),
-- Floor 3
(11, '301', 3, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '3001', NOW(), NOW(), 1),
(12, '302', 3, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '3002', NOW(), NOW(), 1),
(13, '303', 3, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '3003', NOW(), NOW(), 1),
(14, '304', 3, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='DND' LIMIT 1), 4, '3004', NOW(), NOW(), 1),
(15, '305', 3, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='VC' LIMIT 1), 2, '3005', NOW(), NOW(), 1),
-- Floor 4
(16, '401', 4, 4, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 2, '4001', NOW(), NOW(), 1),
(17, '402', 4, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OC' LIMIT 1), 4, '4002', NOW(), NOW(), 1),
(18, '403', 4, 2, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='VACANT'   LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='INS' LIMIT 1), 2, '4003', NOW(), NOW(), 1),
(19, '404', 4, 1, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='OCCUPIED' LIMIT 1),  (SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='OD' LIMIT 1), 2, '4004', NOW(), NOW(), 1),
(20, '405', 4, 3, (SELECT id FROM common_masters WHERE category='ROOM_STATUS' AND code='MAINTENANCE' LIMIT 1),(SELECT id FROM common_masters WHERE category='HK_STATUS' AND code='UM' LIMIT 1), 4, '4005', NOW(), NOW(), 1);

-- -------------------------------------------------------
-- ROOM BOOKINGS  (FY 2026-27: April 2026 – March 2027)
-- Spread across all 12 months with realistic revenue
-- -------------------------------------------------------
INSERT IGNORE INTO room_bookings (room_id, guest_name, booking_date, check_in_date, check_out_date, total_amount, status) VALUES
-- April 2026
(1,  'Arun Sharma',    '2026-04-03 10:00:00', '2026-04-03', '2026-04-06', 10500.00, 'CHECKED_OUT'),
(4,  'Priya Nair',     '2026-04-10 11:00:00', '2026-04-10', '2026-04-15', 47500.00, 'CHECKED_OUT'),
(9,  'Ravi Pillai',    '2026-04-18 09:00:00', '2026-04-18', '2026-04-22', 38000.00, 'CHECKED_OUT'),
(12, 'Sunita Menon',   '2026-04-25 14:00:00', '2026-04-25', '2026-04-28', 21000.00, 'CHECKED_OUT'),
-- May 2026
(2,  'Kavya Reddy',    '2026-05-02 10:00:00', '2026-05-02', '2026-05-05', 16500.00, 'CHECKED_OUT'),
(6,  'Arjun Bose',     '2026-05-12 12:00:00', '2026-05-12', '2026-05-17', 35000.00, 'CHECKED_OUT'),
(17, 'Nisha Gupta',    '2026-05-20 11:00:00', '2026-05-20', '2026-05-25', 47500.00, 'CHECKED_OUT'),
(3,  'Rakesh Kumar',   '2026-05-28 09:00:00', '2026-05-28', '2026-05-31', 16500.00, 'CHECKED_OUT'),
-- June 2026
(14, 'Deepa Iyer',     '2026-06-05 10:00:00', '2026-06-05', '2026-06-10', 47500.00, 'CHECKED_OUT'),
(7,  'Mohan Das',      '2026-06-15 14:00:00', '2026-06-15', '2026-06-18', 10500.00, 'CHECKED_OUT'),
(16, 'Anjali Singh',   '2026-06-22 11:00:00', '2026-06-22', '2026-06-27', 35000.00, 'CHECKED_OUT'),
-- July 2026
(4,  'Vinod Khanna',   '2026-07-04 10:00:00', '2026-07-04', '2026-07-09', 47500.00, 'CHECKED_OUT'),
(11, 'Meena Joshi',    '2026-07-14 12:00:00', '2026-07-14', '2026-07-17', 10500.00, 'CHECKED_OUT'),
(9,  'Suresh Patel',   '2026-07-22 09:00:00', '2026-07-22', '2026-07-28', 57000.00, 'CHECKED_OUT'),
(1,  'Latha Krishnan', '2026-07-30 11:00:00', '2026-07-30', '2026-08-03', 14000.00, 'CHECKED_OUT'),
-- August 2026
(17, 'Hari Varma',     '2026-08-06 10:00:00', '2026-08-06', '2026-08-12', 57000.00, 'CHECKED_OUT'),
(3,  'Sona Roy',       '2026-08-15 13:00:00', '2026-08-15', '2026-08-20', 27500.00, 'CHECKED_OUT'),
(6,  'Kiran Mehta',    '2026-08-25 11:00:00', '2026-08-25', '2026-08-29', 28000.00, 'CHECKED_OUT'),
-- September 2026
(14, 'Diya Prabhu',    '2026-09-03 10:00:00', '2026-09-03', '2026-09-08', 47500.00, 'CHECKED_OUT'),
(2,  'Anil Kumar',     '2026-09-12 14:00:00', '2026-09-12', '2026-09-16', 22000.00, 'CHECKED_OUT'),
(12, 'Rekha Nair',     '2026-09-20 11:00:00', '2026-09-20', '2026-09-25', 47500.00, 'CHECKED_OUT'),
(1,  'Sanjay Ware',    '2026-09-27 09:00:00', '2026-09-27', '2026-09-30', 10500.00, 'CHECKED_OUT'),
-- October 2026
(4,  'Tanvi Shah',     '2026-10-05 10:00:00', '2026-10-05', '2026-10-10', 47500.00, 'CHECKED_OUT'),
(17, 'Rohit Malhotra', '2026-10-14 12:00:00', '2026-10-14', '2026-10-20', 57000.00, 'CHECKED_OUT'),
(9,  'Geeta Pillai',   '2026-10-25 11:00:00', '2026-10-25', '2026-10-30', 47500.00, 'CHECKED_OUT'),
(6,  'Manoj Tiwari',   '2026-10-31 13:00:00', '2026-10-31', '2026-11-04', 28000.00, 'CHECKED_OUT'),
-- November 2026
(14, 'Preeti Jain',    '2026-11-08 10:00:00', '2026-11-08', '2026-11-13', 47500.00, 'CHECKED_OUT'),
(3,  'Nitin Verma',    '2026-11-16 11:00:00', '2026-11-16', '2026-11-20', 22000.00, 'CHECKED_OUT'),
(12, 'Usha Rao',       '2026-11-25 09:00:00', '2026-11-25', '2026-11-30', 47500.00, 'CHECKED_OUT'),
-- December 2026
(4,  'Vikram Dixit',   '2026-12-02 10:00:00', '2026-12-02', '2026-12-08', 57000.00, 'CHECKED_OUT'),
(17, 'Pooja Mehta',    '2026-12-12 14:00:00', '2026-12-12', '2026-12-18', 57000.00, 'CHECKED_OUT'),
(9,  'Rajan Sood',     '2026-12-20 11:00:00', '2026-12-20', '2026-12-27', 66500.00, 'CHECKED_OUT'),
(1,  'Smita Deshpande','2026-12-28 09:00:00', '2026-12-28', '2027-01-02', 17500.00, 'CHECKED_OUT'),
-- January 2027
(6,  'Ashwin Naik',    '2027-01-04 10:00:00', '2027-01-04', '2027-01-09', 35000.00, 'CHECKED_OUT'),
(14, 'Madhuri Sen',    '2027-01-12 12:00:00', '2027-01-12', '2027-01-18', 57000.00, 'CHECKED_OUT'),
(3,  'Dinesh Bhatt',   '2027-01-22 11:00:00', '2027-01-22', '2027-01-27', 27500.00, 'CHECKED_OUT'),
(12, 'Shalini Joshi',  '2027-01-28 13:00:00', '2027-01-28', '2027-01-31', 28500.00, 'CHECKED_OUT'),
-- February 2027
(4,  'Rahul Srivastava','2027-02-05 10:00:00','2027-02-05', '2027-02-10', 47500.00, 'CHECKED_OUT'),
(17, 'Kavitha Nair',   '2027-02-13 11:00:00', '2027-02-13', '2027-02-18', 47500.00, 'CHECKED_OUT'),
(9,  'Prakash Iyer',   '2027-02-22 09:00:00', '2027-02-22', '2027-02-26', 38000.00, 'CHECKED_OUT'),
-- March 2027
(6,  'Nalini Reddy',   '2027-03-03 10:00:00', '2027-03-03', '2027-03-08', 35000.00, 'CHECKED_OUT'),
(14, 'Aditya Kumar',   '2027-03-12 14:00:00', '2027-03-12', '2027-03-17', 47500.00, 'CHECKED_OUT'),
(4,  'Swati Patil',    '2027-03-20 11:00:00', '2027-03-20', '2027-03-25', 47500.00, 'CHECKED_OUT'),
(1,  'Ganesh Murty',   '2027-03-27 09:00:00', '2027-03-27', '2027-03-31', 14000.00, 'CHECKED_IN');

-- -------------------------------------------------------
-- OUTLETS
-- -------------------------------------------------------
INSERT IGNORE INTO outlets (id, name, type_id, location, timing, tax_profile, is_active, created_at, updated_at) VALUES
(1, 'The Grand Restaurant', (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='RESTAURANT' LIMIT 1), 'Ground Floor', '07:00-23:00', 'GST_5', 1, NOW(), NOW()),
(2, 'Skybar Lounge',        (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='BAR'        LIMIT 1), 'Terrace',       '12:00-02:00', 'GST_18',1, NOW(), NOW()),
(3, 'Morning Cafe',         (SELECT id FROM common_masters WHERE category='OUTLET_TYPE' AND code='CAFE'       LIMIT 1), 'Lobby',         '06:00-12:00', 'GST_5', 1, NOW(), NOW());

-- -------------------------------------------------------
-- MENU ITEMS
-- -------------------------------------------------------
INSERT IGNORE INTO menu_items (id, outlet_id, item_name, category_id, price, tax_percent, is_available, is_featured, created_at, updated_at) VALUES
-- Restaurant
(1,  1, 'Paneer Tikka',      (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 350.00, 5.00, 1, 1, NOW(), NOW()),
(2,  1, 'Veg Biryani',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 450.00, 5.00, 1, 1, NOW(), NOW()),
(3,  1, 'Butter Chicken',    (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 520.00, 5.00, 1, 0, NOW(), NOW()),
(4,  1, 'Dal Makhani',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 380.00, 5.00, 1, 0, NOW(), NOW()),
(5,  1, 'Gulab Jamun',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='DESSERT'  LIMIT 1), 180.00, 5.00, 1, 0, NOW(), NOW()),
(6,  1, 'Masala Chai',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1),  80.00, 5.00, 1, 0, NOW(), NOW()),
-- Skybar
(7,  2, 'Tropical Punch',    (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 450.00, 18.00, 1, 1, NOW(), NOW()),
(8,  2, 'Beer Pint',         (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 380.00, 18.00, 1, 0, NOW(), NOW()),
(9,  2, 'Nachos with Dip',   (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 290.00, 18.00, 1, 0, NOW(), NOW()),
(10, 2, 'Grilled Chicken',   (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='MAIN'     LIMIT 1), 650.00, 18.00, 1, 1, NOW(), NOW()),
-- Cafe
(11, 3, 'Espresso',          (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 120.00, 5.00, 1, 1, NOW(), NOW()),
(12, 3, 'Croissant',         (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='STARTERS' LIMIT 1), 150.00, 5.00, 1, 1, NOW(), NOW()),
(13, 3, 'Cold Coffee',       (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='BEVERAGE' LIMIT 1), 180.00, 5.00, 1, 0, NOW(), NOW()),
(14, 3, 'Waffles',           (SELECT id FROM common_masters WHERE category='MENU_CATEGORY' AND code='DESSERT'  LIMIT 1), 220.00, 5.00, 1, 0, NOW(), NOW());

-- -------------------------------------------------------
-- POS ORDERS & ITEMS  (FY 2026-27, spread across months)
-- -------------------------------------------------------
INSERT IGNORE INTO pos_orders (id, outlet_id, status_id, total_amount, created_at, updated_at) VALUES
(1,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1580.00, '2026-04-05 13:30:00', '2026-04-05 14:00:00'),
(2,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1240.00, '2026-05-10 20:00:00', '2026-05-10 20:45:00'),
(3,  2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2160.00, '2026-06-15 21:00:00', '2026-06-15 21:30:00'),
(4,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1830.00, '2026-07-20 13:00:00', '2026-07-20 13:45:00'),
(5,  3, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1),  450.00, '2026-08-08 09:00:00', '2026-08-08 09:20:00'),
(6,  2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 3120.00, '2026-09-22 22:00:00', '2026-09-22 22:45:00'),
(7,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2050.00, '2026-10-12 14:00:00', '2026-10-12 14:30:00'),
(8,  3, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1),  620.00, '2026-11-18 08:30:00', '2026-11-18 09:00:00'),
(9,  1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2890.00, '2026-12-24 20:00:00', '2026-12-24 21:00:00'),
(10, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 4200.00, '2027-01-01 23:00:00', '2027-01-01 23:45:00'),
(11, 1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 1760.00, '2027-02-14 20:00:00', '2027-02-14 20:45:00'),
(12, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='CLOSED' LIMIT 1), 2980.00, '2027-03-08 21:00:00', '2027-03-08 22:00:00'),
-- Active open orders (for KOT queue)
(13, 1, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='OPEN'   LIMIT 1), 0.00,    NOW(),                 NOW()),
(14, 2, (SELECT id FROM common_masters WHERE category='POS_ORDER_STATUS' AND code='KOT_SENT' LIMIT 1), 0.00, NOW(),                 NOW());

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
INSERT IGNORE INTO departments (id, name, description, is_active, created_at, updated_at) VALUES
(1, 'Housekeeping', 'Room cleaning and maintenance', 1, NOW(), NOW()),
(2, 'F&B Service',  'Food and beverage service',      1, NOW(), NOW()),
(3, 'Maintenance',   'Engineering and repairs',       1, NOW(), NOW());

-- -------------------------------------------------------
-- 14. ROLES (New Table)
-- -------------------------------------------------------
INSERT IGNORE INTO roles (id, name, department_id, access_level, status, description) VALUES
(1, 'Property Admin', 1, 'Admin',      'Active', 'Full system access'),
(2, 'HK Supervisor',  1, 'Supervisor', 'Active', 'Managing HK operations'),
(3, 'Room Attendant', 1, 'Department', 'Active', 'Cleaning rooms'),
(4, 'Server',         2, 'Department', 'Active', 'Serving food in outlets');

-- -------------------------------------------------------
-- USERS (Staff) - Linked to departments, roles, and hotels
-- -------------------------------------------------------
INSERT IGNORE INTO users (id, employee_id, full_name, username, email, phone, department_id, role_id, property_id, status, created_at, updated_at) VALUES
(1, 'EMP-101', 'Admin User', 'admin', 'admin@hms.com', '9800010001', 1, 1, 1, 'ACTIVE', NOW(), NOW()),
(2, 'EMP-102', 'Sarah Wilson', 'sarah', 'sarah@hms.com', '9800010002', 1, 2, 1, 'ACTIVE', NOW(), NOW()),
(3, 'EMP-103', 'John Doe', 'john', 'john@hms.com', '9800010003', 1, 3, 1, 'ACTIVE', NOW(), NOW()),
(4, 'EMP-104', 'Mike Ross', 'mike', 'mike@hms.com', '9800010004', 2, 4, 1, 'ACTIVE', NOW(), NOW());

-- -------------------------------------------------------
-- DINING TABLES
-- -------------------------------------------------------
INSERT IGNORE INTO dining_tables (id, outlet_id, table_number, status_id, covers, created_at, updated_at) VALUES
(1, 1, 'T-01', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='OCCUPIED' LIMIT 1), 4, NOW(), NOW()),
(2, 1, 'T-02', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='AVAILABLE' LIMIT 1), 2, NOW(), NOW()),
(3, 1, 'T-03', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='RESERVED'  LIMIT 1), 4, NOW(), NOW()),
(4, 2, 'B-01', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='OCCUPIED' LIMIT 1), 2, NOW(), NOW()),
(5, 2, 'B-02', (SELECT id FROM common_masters WHERE category='TABLE_STATUS' AND code='CLEANING' LIMIT 1), 2, NOW(), NOW());

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
INSERT IGNORE INTO tasks (id, room_id, task_type, priority, assigned_user_id, status, created_at, updated_at) VALUES
(1, 2, 'Cleaning', 'MEDIUM', 3, 'PENDING',     NOW(), NOW()),
(2, 5, 'Cleaning', 'HIGH',   3, 'IN_PROGRESS', NOW(), NOW()),
(3, 7, 'Cleaning', 'LOW',    3, 'COMPLETED',   NOW(), NOW()),
(4, 11,'Cleaning', 'HIGH',   3, 'PENDING',     NOW(), NOW());

-- -------------------------------------------------------
-- MAINTENANCE REQUESTS
-- -------------------------------------------------------
INSERT IGNORE INTO maintenance_requests (id, room_id, repair_issue, category_id, priority_id, reported_by_id, status, reported_at, updated_at) VALUES
(1, 8, 'AC leaking water', 
    (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='MAINT' LIMIT 1),
    (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 3, 'OPEN', NOW(), NOW()),
(2, 20,'TV remote not working', 
    (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='MAINT' LIMIT 1),
    (SELECT id FROM common_masters WHERE category='SOP_FREQUENCY' AND code='DAILY' LIMIT 1), 3, 'IN_PROGRESS', NOW(), NOW());

-- -------------------------------------------------------
-- LOST AND FOUND ITEMS
-- -------------------------------------------------------
INSERT IGNORE INTO lost_found_items (id, room_id, item_description, category_id, found_by_id, storage_location, found_date, status, created_at, updated_at) VALUES
(1, 1, 'iPhone 13 Charger', (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='HK' LIMIT 1), 3, 'HK Locker A-12', '2026-06-10', 'STORED', NOW(), NOW()),
(2, 9, 'Blue Wallet',       (SELECT id FROM common_masters WHERE category='DEPARTMENT' AND code='HK' LIMIT 1), 3, 'Safe Box',        '2026-06-11', 'STORED', NOW(), NOW());

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