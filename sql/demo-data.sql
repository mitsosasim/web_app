-- Fictional local demo data. Passwords are public demonstration credentials.
INSERT INTO petowners (owner_id,username,email,password,firstname,lastname,birthdate,gender,country,city,address,personalpage,job,telephone,lat,lon) VALUES
(1,'demo_owner','owner@example.invalid','Demo123!','Demo','Owner','1995-01-01','other','Greece','Heraklion','Example Street 1','https://example.invalid','Student','0000000000',35.3387,25.1442);
INSERT INTO petkeepers (keeper_id,username,email,password,firstname,lastname,birthdate,gender,country,city,address,personalpage,job,telephone,lat,lon,property,propertydescription,catkeeper,dogkeeper,catprice,dogprice) VALUES
(1,'demo_keeper','keeper@example.invalid','Demo123!','Alex','Keeper','1993-01-01','other','Greece','Heraklion','Example Street 2','https://example.invalid','Pet sitter','0000000001',35.3360,25.1350,'House','Enclosed garden','false','true',0,20),
(2,'demo_catkeeper','cats@example.invalid','Demo123!','Maria','Keeper','1992-01-01','other','Greece','Heraklion','Example Street 3','https://example.invalid','Pet sitter','0000000002',35.3400,25.1300,'Apartment','Quiet indoor space','true','false',15,0),
(3,'demo_newkeeper','new@example.invalid','Demo123!','Nikos','Keeper','1990-01-01','other','Greece','Heraklion','Example Street 4','https://example.invalid','Pet sitter','0000000003',35.3300,25.1400,'House','Fenced outdoor space','false','true',0,25);
INSERT INTO pets (pet_id,owner_id,name,type,breed,gender,birthyear,weight,description,photo) VALUES
('1000000001',1,'Milo','dog','Mixed','male',2020,12,'Friendly demo dog',''),
('1000000002',1,'Luna','cat','Mixed','female',2021,4,'Calm demo cat','');
INSERT INTO bookings (booking_id,owner_id,pet_id,keeper_id,fromdate,todate,status,price) VALUES
(1,1,'1000000001',1,'2026-11-01','2026-11-03','requested',40),
(2,1,'1000000002',2,'2026-10-01','2026-10-03','finished',30);
INSERT INTO messages (message_id,booking_id,message,sender,datetime) VALUES
(1,1,'Milo enjoys a short walk in the morning.','owner','2026-10-20 10:00:00');
INSERT INTO reviews (review_id,owner_id,keeper_id,reviewText,reviewScore) VALUES
(1,1,2,'Luna was well looked after.',5);
