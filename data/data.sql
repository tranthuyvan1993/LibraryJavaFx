-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th8 26, 2022 lúc 11:12 AM
-- Phiên bản máy phục vụ: 10.4.21-MariaDB
-- Phiên bản PHP: 8.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
-- SET SQL_MODE='ALLOW_INVALID_DATES';
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `library`
--
-- drop database if exists `library`;
CREATE DATABASE IF NOT EXISTS `library` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `library`;
-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `admin`
--

CREATE TABLE IF NOT EXISTS `admin` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `fullname` varchar(50) NOT NULL,
  `username` varchar(20) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `password` varchar(80) NOT NULL,
  `avatar` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `role` varchar(20) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `two_auth` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
   UNIQUE KEY `username` (`username`),
   UNIQUE KEY `email` (`email`),
   UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

--
-- Đang đổ dữ liệu cho bảng `admin`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `book`
--

CREATE TABLE IF NOT EXISTS `book` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `price` long NOT NULL,
  `description` text,
  `publisher` varchar(50),
  `auth` varchar(50),
  `image` varchar(50) NOT NULL,
  `category_id` int(4) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `book_detail`
--

CREATE TABLE IF NOT EXISTS `book_detail` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `book_id` int(4) NOT NULL,
  `import_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `note` text,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `book_id` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `borrow`
--

CREATE TABLE IF NOT EXISTS `borrow` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `reader_id` int(4) NOT NULL,
  `book_detail_id` int(4) NOT NULL,
  `borrow_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `due_date` timestamp NOT NULL,
  `return_date` timestamp NULL DEFAULT NULL,
  `note` text,
  `status` tinyint(4) NOT NULL DEFAULT 0,
PRIMARY KEY (`id`),
  KEY `book_detail_id` (`book_detail_id`),
  KEY `reader_id` (`reader_id`)


) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `category`
--

CREATE TABLE IF NOT EXISTS `category` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reader`
--

CREATE TABLE IF NOT EXISTS `reader` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `avatar` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `address` text,
  `status` tinyint(4) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `accept_email` tinyint(1) NOT NULL DEFAULT 0,
  `gender` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Cấu trúc bảng cho bảng `announce`
--

CREATE TABLE IF NOT EXISTS `announce` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `user_id` int(4) NOT NULL,
  `announce_time` timestamp NOT NULL,
  `announce_type` varchar(50) NOT NULL,
  `target_id` int(4),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `book`
--
ALTER TABLE `book`
  ADD CONSTRAINT `book_ibfk_1` FOREIGN KEY IF NOT EXISTS  (`category_id`) REFERENCES `category` (`id`);

--
-- Các ràng buộc cho bảng `book_detail`
--
ALTER TABLE `book_detail`
  ADD CONSTRAINT `book_detail_ibfk_1` FOREIGN KEY IF NOT EXISTS (`book_id`) REFERENCES `book` (`id`);

--
-- Các ràng buộc cho bảng `borrow`
--
ALTER TABLE `borrow`
  ADD CONSTRAINT `borrow_ibfk_1` FOREIGN KEY IF NOT EXISTS (`book_detail_id`)  REFERENCES `book_detail` (`id`),
  ADD CONSTRAINT `borrow_ibfk_2` FOREIGN KEY IF NOT EXISTS (`reader_id`) REFERENCES `reader` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

INSERT INTO `admin` VALUES (1000,'Tran Thuy Van','admin','0965734293','$2a$12$WNg9riovPlyECKxcOgvaNuStxpcQZ/9qnlIzA2AFc/fAe4aZbaFQG','1663404780431_0965734293.jpg','vantranthuy76@gmail.com','SUPER_ADMIN',1,1),(1001,'Tran Duc Loc','admin_loc','0948738474','$2a$12$BfQy3FTt.ikxZ95QCGxUAO/hIKqz/p64hVIL10h7I5b.rvF9/bLai','1663387036239_0948738474.jpg','loctran1906@gmail.com','ADMIN',1,0),(1002,'Nguyen Thinh Trinh','admin_trinh','0934540000','$2a$12$mEI2v3f1Egea1ncaJc8kneA/zmsgxlY.Ostk0plDvIFwEmtQOq9Ou','1663389224461_0934540000.jpg','thinhtrinh@gmail.com','READER_MANAGER',1,0),(1003,'Hoang Hai Nam','admin123','0917589589','$2a$12$KgsPZyZv532T2v9SkKy1luyCouXjKoCEyjYX8Am5mF.HR2Dm/IZJe','1663390692580_0917589588.jpg','van.nt.1071@aptechlearning.edu.vn','ADMIN_MANAGER',1,1),(1004,'Hoang Hai Nam','hainam','0949499499','$2a$12$Lo.a5BKlQzXk4Z4.3F7nNuasbWL1PNhYiiNhrtcIvWnOz24anjLpG','1663390912223_0949499499.jpg','van.nt.1071@aptechlearning','READER_MANAGER',1,0),(1005,'Mai An An','admin101','0940394445','$2a$12$RrRjRfJwMNZG/ovwIyIhsOCFYD5s1aEJYtdsPBV0n4WhV1ouSkWB6','1663392394689_0940394445.jpg','vantranthuy76@gmail','READER_MANAGER',1,0),(1006,'Hoang Mai','maimai','0394094555','$2a$12$dGYm/H6/C5/oY8/S8U5rP.TofF.3XPdEuULReTSc4PyCjx71iUEoi','1663392478263_0394094555.jpg','mai@gmail','ADMIN',0,0);
INSERT INTO `category` VALUES (1,'Tiểu thuyết tình cảm','Tiểu thuyết lãng mạn có lẽ là một trong số các thể loại sách phổ biến nhất khi so về doanh số bán sách. Tiểu thuyết lãng mạn sở hữu một quy mô kinh doanh đa dạng nhất trong thị trường sách, các tác phẩm này được trưng bày khắp nơi từ các quầy thanh toán ở cửa hàng tạp hóa, đến các ấn phẩm định kỳ của nhà xuất bản trên nền tảng trực tuyến, cũng như thông qua các dịch vụ tự xuất bản.'),(2,'Truyện ngắn','Nó thường là các câu chuyện kể bằng văn xuôi và có xu hướng ngắn gọn, súc tích và hàm nghĩa. Truyện ngắn thường chỉ tập trung vào một tình huống, một chủ đề nhất định. Truyện ngắn được định nghĩa là tác phẩm tự sự cỡ nhỏ. Nội dung thể loại của Truyện ngắn bao trùm hầu hết các phương diện của đời sống: đời tư, thế sự hay sử thi, nhưng cái độc đáo của nó là ngắn gọn.'),(3,'Khoa học viễn tưởng','Sách giả tưởng thường diễn ra trong một khoảng thời gian khác với thời gian hiện tại của chúng ta. Chúng thường có các sinh vật huyền bí, từ pháp sư / phù thuỷ cho đến những thây ma không có thật.'),(4,'Kinh dị','Các thể loại sách này bao gồm các ấn phẩm thường có mối liên hệ mật thiết đến những thể loại sách Mystery và đôi khi là giả tưởng – Fantasy, yếu tố ly kỳ và kinh dị tạo nên sự hồi hộp và điểm nổi bật của thể loại sách phổ biến này. Các tác giả như David Baldacci và Dan Brown là một trong những ngòi bút thống trị danh sách sách bán chạy nhất với các tựa phim kinh dị của họ, trong khi Stephen King lại là bậc thầy của thể loại kinh dị đương đại.'),(5,'Lịch sử','Những cuốn sách này ghi lại và bố cục một thời điểm cụ thể, với mục tiêu giáo dục và cung cấp thông tin cho người đọc, về mọi nơi trên thế giới tại bất kỳ thời điểm nào. Thể loại sách lịch sử cực kỳ hấp dẫn đối với những người yêu thích tìm hiểu về các nhân vật, triều đại trong quá khứ.'),(6,'Bí ẩn','Tiểu thuyết bí ẩn bắt đầu bằng một câu chuyện hấp dẫn, khiến người đọc thích thú với nhịp độ hồi hộp và kết thúc bằng một cái kết thỏa mãn trả lời tất cả các câu hỏi nổi bật của người đọc. Các nhánh phụ của thể loại sách bí ẩn bao gồm những tác phẩm Cozy mystery (Trinh thám bí ẩn hấp dẫn), tiểu thuyết tội phạm có thật, bí ẩn khoa học, những câu chuyện trinh thám hấp dẫn và các câu chuyện điều tra của cảnh sát.');
INSERT INTO `book` VALUES (113,'Cuốn theo chiều gió','250000','Cuốn theo chiều gió (Nguyên văn: Gone with the wind), xuất bản lần đầu năm 1936, là một cuốn tiểu thuyết tình cảm của Margaret Mitchell, người đã giành giải Pulitzer với tác phẩm này năm 1937. Câu chuyện được đặt bối cảnh tại Georgia và Atlanta, miền Nam Hoa Kỳ trong suốt thời kì nội chiến và thời tái thiết. Tác phẩm xoay quanh Scarlett O Hara, một cô gái miền Nam đầy sức mạnh, phải tìm mọi cách để sống sót qua chiến tranh và vượt lên cuộc sống khó khăn trong thời hậu chiến. Tiểu thuyết đã được chuyển thể thành phim năm 1937.','NXB Hà Nội','Mitchell','1663389341586_1.A.1.1.1.jpg',1),(114,'Kiêu hãnh và định kiến','215000','Kiêu hãnh và định kiến là tác phẩm nổi tiếng nhất của nhà văn Anh Jane Austen. Tiểu thuyết được viết từ năm 1796 đến năm 1797 và xuất bản năm 1813. Câu chuyện nói về tình yêu và hôn nhân của tầng lớp quý tộc nhỏ tại Anh vào đầu thế kỷ 19','Kim Dong','Jane Austen','1663395055097_2.B.1.2.8.jpg',1),(115,'Đồi gió hú','135500','Sách được xuất bản năm 1847 dưới bút danh Ellis Bell. Tên tiểu thuyết bắt nguồn từ một trang viên nằm trên vùng đồng cỏ hoang dã ở Yorkshire – nơi những sự kiện trong tiểu thuyết ra. Tiểu thuyết xoay quanh câu chuyện tình yêu không thành giữa Heathcliff và Catherine Earnshaw, cũng như làm thế nào mà sự đam mê không thể hóa giải đó đã tiêu diệt chính họ và cả những người thân khác xung quanh.','Văn học VN','Emily Bond','1663394423982_1.A.1.1.2.jpg',6),(116,'Đôi mắt','50000','','Văn Học VN','Nam Cao','1663394928620_2.A.1.2.9.png',4),(117,'Lão Hạc','66000','','Kim Đồng','Nam Cao','1663394509422_4.A.2.1.5.jpg',2),(118,'Số Đỏ','166000','“Số đỏ” là một trong những cuốn tiểu thuyết nổi tiếng nhất của Nhà văn Vũ Trọng Phụng trong dòng văn học hiện thực phê phán những năm 1930-1945. Ra đời từ những năm 30 của thế kỷ XX, nhưng đến nay, tính châm biếm trào phúng của \"Số đỏ\" vẫn còn nguyên giá trị trong xã hội đương thời. Tác phẩm đã nhiều lần được dựng thành vở diễn sân khấu, phim truyền hình, phim điện ảnh…','Văn học VN','Vũ Trọng Phụng','1663394968159_1.B.2.1.2.jpg',5),(119,'Đời thừa','35000','Đời thừa viết về cuộc sống của một trí thức nghèo, một nhà văn. Hộ là một con người trung thực, thương yêu vợ con, rất có trách nhiệm đối với gia đình, là một người cầm bút có suy nghĩ đúng đắn, nghiêm túc về nghề pghiệp, có hoài bão xây dựng được mội tác phẩm thật có giá trị \"sẽ làm mờ hết các tác phẩm cùng ra một thời\"','Văn Học VN','Nam Cao','1663400236495_4.A.2.2.5.jpg',2),(120,'Không gia đình','120000','\"KHÔNG GIA ĐÌNH\" là tiểu thuyết nổi tiếng nhất trong sự nghiệp văn chương của Hector Malot. Hơn một trăm năm nay, tác phẩm giành giải thưởng của Viện Hàn Lâm Văn học Pháp này đã trở thành người bạn thân thiết của tất cả những người yêu mến trẻ trên khắp thế giới.','Kim Đồng','Hecto Malot','1663400331883_1.C.2.2.10.jpg',5),(121,'Người tù khổ sai','125000','Papillon Người Tù Khổ Sai là một thiên hồi kí của Henri Charrière kể lại chính cuộc đời mình, về hành trình từ khi bị bắt giam và xử án đầy khổ sai chung thân vì tội giết người theo lời khai của một nhân chứng đã được “dàn xếp” trước. Charrière đã quyết chí vượt ngục ngay từ đầu. Ông quyết sống và ra khỏi trại khổ sai để trả thù. Quyết tâm ấy đã làm cho ông có đủ sức mạnh chịu đựng mọi thử thách. Không có một mối nguy hiểm nào làm cho ông lùi bước, không một phen thất bại nào làm cho ông nhụt chí. Charrière với biệt hiệu là bươm bướm, đã tổ chức cả thẩy chín lần vượt ngục trước khi thành công và được nhận cư trú ở Venezuela như một công dân chính thức.','Kim Đồng','Henri Charriere','1663400422420_1.A.2.2.5.jpg',3),(122,'Tiếng chim hót trong bụi mận gai','165000','Tiếng chim hót trong bụi mận gai (nguyên bản: The Thorn Birds, còn được gọi theo bản dịch tiếng Pháp là Những con chim ẩn mình chờ chết/Les oiseaux se cachent pour mourir) là tiểu thuyết nổi tiếng nhất của nữ văn sĩ người Úc Colleen McCullough, được xuất bản năm 1977.','Phụ nữ','Colleen Mc','1663400560892_1.B.1.1.2.jpg',4),(123,'Vỡ đê','45000','Vỡ đê được đăng tải trên mặt báo vào năm 1936 và \nlà một trong bốn thiên tiểu thuyết đặc sắc của nhà văn đất Bắc Vũ Trọng Phụng. \nVới ngòi bút tả chân điêu luyện và giọng điệu trào phúng quen thuộc, nhà văn đã thành công tái hiện chi tiết cả một giai đoạn bát nháo của xã hội Việt Nam trước Cách mạng tháng Tám qua từng trang sách.','Văn học','Vũ Trọng Phụng','1663400793718_4.B.1.2.4.jpg',5),(124,'Chí Phèo','45000','Chí Phèo là một truyện ngắn nổi tiếng của nhà văn Nam Cao viết vào tháng 2 năm 1941. Chí Phèo là một tác phẩm xuất sắc, thể hiện nghệ thuật viết truyện độc đáo của Nam Cao, đồng thời là một tấn bi kịch của một người nông dân nghèo bị tha hóa trong xã hội. Chí Phèo cũng là tên nhân vật chính của truyện.','Kim Đồng','Nam Cao','1663400934304_4.B.1.2.5.jpg',3);
INSERT INTO `book_detail` VALUES (108,'1.A.1.1.1',113,'2022-09-17 04:35:42','Sách hay cực',1),(109,'2.B.1.2.8',114,'2022-09-17 05:07:29','Sách mới',1),(110,'1.A.1.1.2',115,'2022-09-17 06:00:24','Sách mới',1),(111,'2.A.1.2.9',116,'2022-09-17 06:01:07','Sách rách bìa',1),(112,'4.A.2.1.5',117,'2022-09-17 06:01:49','Sách mất bìa',1),(113,'1.B.2.1.2',118,'2022-09-17 06:03:50','Sách mới',1),(114,'4.A.2.2.5',119,'2022-09-17 07:37:16','Sách mới',1),(115,'1.C.2.2.10',120,'2022-09-17 07:38:52','',1),(116,'1.A.2.2.5',121,'2022-09-17 07:40:22','Sách mất trang số 10',1),(117,'1.B.1.1.2',122,'2022-09-17 07:42:41','Sách mới',1),(118,'4.B.1.2.4',123,'2022-09-17 07:46:33','sách mới',1),(119,'4.B.1.2.5',124,'2022-09-17 07:48:54','Sách mới',1);
INSERT INTO `reader` VALUES (101,'79344610','Tran Mai Mai','0944444444','1663389686311_0944444444.jpg','mai@gmail.com','',1,'2022-09-17 04:41:26',0,'FEMALE'),(102,'06478149','Nguyen The An','0934094444','1663389740598_0934094444.jpg','anan@gmail.com','',1,'2022-09-17 04:42:20',0,'FEMALE'),(103,'45775425','Pham Van Hai','0394503949','1663389773519_0394503949.jpg','hai@gmail.com','',1,'2022-09-18 04:42:53',0,'MALE'),(104,'04569780','Nguyen The Hien','0394094445','1663389819041_0394094445.jpg','nguyenthinhtrinh1999@gmail.com','',1,'2022-09-19 04:43:39',0,'FEMALE'),(105,'37099341','Hoang Thai Minh','0344849855','1663390079661_0344849855.jpg','minh@gmail.com','',1,'2022-09-19 04:47:59',1,'MALE'),(106,'37364336','Tran The Hai','0394039445','1663390116364_0394039445.jpg','hai12@gmail.com','',0,'2022-09-20 04:48:36',0,'MALE'),(107,'76269568','Mai An Tiem','0934055858','1663390141644_0934055858.jpg','maimai@gmail.com','',0,'2022-09-20 04:49:01',0,'FEMALE'),(108,'23090835','Nguyen Ngoc Ngan','0884758475','1663390182832_0884758475.jpg','ngoc@gmail.com','Ha Noi',1,'2022-09-20 04:49:42',1,'FEMALE'),(109,'52474481','Pham Tien Tien','0948545767','1663390222669_0948545767.jpg','tien@gmail.com','',0,'2022-09-21 04:50:22',0,'MALE'),(110,'99836775','Nguyen Quoc Huy','0963746858','1663390268924_0963746858.png','huy@gmail.com','Nam Dinh',1,'2022-09-21 04:51:08',0,'MALE'),(111,'90611790','Pham Quang Hai','0876473643','1663390325490_0876473643.jpg','haihai@gmail.com','Ha Noi',1,'2022-09-22 04:52:05',1,'MALE'),(112,'40550186','Tran Duc Bo','0934039444','1663390360996_0934039444.jpg','loctran@gmail.com','Nam Dinh',0,'2022-09-22 04:52:41',0,'MALE'),(113,'70362353','Hai Pham','0394348823','1663390402101_0394348823.jpg','haipham@mail','Hung Yen',1,'2022-09-22 04:53:22',0,'FEMALE'),(114,'23180975','The Hoang','0349049466','1663390511107_0349049466.jpg','hoang@mail','Hai Phong',1,'2022-09-23 04:55:11',1,'MALE'),(115,'26532396','Ngoc Mai','0940594059','1663390604235_0940594059.jpg','mai@mail','Cau Giay, Ha Noi',0,'2022-09-24 04:56:44',0,'FEMALE');
-- INSERT INTO `borrow` VALUES (102,114,108,'2022-09-17 01:00:00','2022-09-22 01:00:00',NULL,NULL,0),(103,111,117,'2022-09-17 15:00:00','2022-09-24 15:00:00',NULL,NULL,0),(104,108,115,'2022-09-17 15:00:00','2022-09-24 15:00:00',NULL,NULL,0),(105,105,118,'2022-09-17 15:00:00','2022-09-30 15:00:00',NULL,NULL,0);
