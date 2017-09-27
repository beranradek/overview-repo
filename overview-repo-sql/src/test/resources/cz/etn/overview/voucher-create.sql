CREATE TABLE IF NOT EXISTS `voucher` (
	`code` VARCHAR(20) NOT NULL,
	`discount_price` DECIMAL(10, 2) NULL DEFAULT NULL,
	`creation_time` DATETIME NOT NULL,
	`valid_from` DATETIME NULL DEFAULT NULL,
	`valid_to` DATETIME NULL DEFAULT NULL,
	`redemption_time` DATETIME NULL DEFAULT NULL,
	`invalidation_time` DATETIME NULL DEFAULT NULL,
	`invalidation_note` VARCHAR(200) NULL DEFAULT NULL,
	`renewal_note` VARCHAR(200) NULL DEFAULT NULL,
	`reserved_by` VARCHAR(40) NULL DEFAULT NULL,
	`redeemed_by` VARCHAR(40) NULL DEFAULT NULL,
	`sold_by` VARCHAR(40) NULL DEFAULT NULL,
	`invoice_time` DATETIME NULL DEFAULT NULL,
	`invoice_note` VARCHAR(400) NULL DEFAULT NULL,
	PRIMARY KEY (`code`)
);
-- COLLATE='utf8_czech_ci'
-- ENGINE=InnoDB

CREATE TABLE IF NOT EXISTS `voucher_customer` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`creation_time` DATETIME NOT NULL,
	`email` VARCHAR(100) NULL DEFAULT NULL,
	`first_name` VARCHAR(60) NULL DEFAULT NULL,
	`last_name` VARCHAR(60) NULL DEFAULT NULL,
	`salutation` VARCHAR(60) NULL DEFAULT NULL,
	`business_partner_code` VARCHAR(20) NULL DEFAULT NULL,
	`discount_email_type` VARCHAR(40) NULL DEFAULT NULL,
	`email_sent_time` DATETIME NULL DEFAULT NULL,
	`email_sending_state` VARCHAR(20) NULL DEFAULT NULL,
	`email_text` TEXT NULL DEFAULT NULL,
	`import_file_name` VARCHAR(255) NOT NULL,
	PRIMARY KEY (`id`)
);
-- COLLATE='utf8_czech_ci'
-- ENGINE=InnoDB

CREATE TABLE IF NOT EXISTS `voucher_supply_point` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`code` VARCHAR(20) NOT NULL,
	`customer_id` INT(11) NOT NULL,
	`creation_time` DATETIME NOT NULL,
	`bonus_points` INT(10) NULL DEFAULT NULL,
	`previous_year_consumption` DECIMAL(16, 4) NULL DEFAULT NULL,
	`current_year_consumption` DECIMAL(16, 4) NULL DEFAULT NULL,
	`consumption_diff` DECIMAL(16, 4) NULL DEFAULT NULL,
	`voucher_discount` DECIMAL(10, 4) NULL DEFAULT NULL,
	`benefit_years` INT(4) NULL DEFAULT NULL,
	`address_street` VARCHAR(80) NULL DEFAULT NULL,
	`address_street_number` VARCHAR(16) NULL DEFAULT NULL,
	`address_city` VARCHAR(60) NULL DEFAULT NULL,
	`address_postal_code` VARCHAR(10) NULL DEFAULT NULL,
	PRIMARY KEY (`id`)
);
-- COLLATE='utf8_czech_ci'
-- ENGINE=InnoDB
