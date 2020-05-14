SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

CREATE DATABASE IF NOT EXISTS `google` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `google`;


-- --------------------------------------------------------
--
-- Table structure for table `urls`
--
DROP TABLE IF EXISTS `urls`;
CREATE TABLE `google`.`urls` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `url` VARCHAR(250) NOT NULL ,
 `popularity` DOUBLE NULL DEFAULT '0.0' ,
 `out_going` INT NOT NULL DEFAULT '0' ,
 `in_going` INT NOT NULL DEFAULT '0' ,
 PRIMARY KEY (`id`)
 ) ENGINE = InnoDB AUTO_INCREMENT=1;
-- --------------------------------------------------------


--
-- Table structure for table `words`
--
DROP TABLE IF EXISTS `words`;
CREATE TABLE `google`.`words` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `word` VARCHAR(45) NOT NULL ,
 `count` INT NOT NULL DEFAULT '1' ,
 `num_of_docs_occurred` INT NOT NULL DEFAULT '1' ,
 PRIMARY KEY (`id`)
 ) ENGINE = InnoDB AUTO_INCREMENT=1 ;
-- --------------------------------------------------------


--
-- Table structure for table `combined`
--
DROP TABLE IF EXISTS `combined`;
CREATE TABLE `google`.`combined` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `url_id` INT NOT NULL ,
 `word_id` INT NOT NULL ,
 `importance` INT NOT NULL ,
 `importance_index` INT NOT NULL ,
 `num_of_occurrences` INT NOT NULL ,
 PRIMARY KEY (`id`),
 KEY `url_id` (`url_id`),
  KEY `word_id` (`word_id`)
 ) ENGINE = InnoDB AUTO_INCREMENT=1 ;
-- --------------------------------------------------------\


--
-- Constraints for table `combined`
--
ALTER TABLE `combined`
  ADD CONSTRAINT `fk_combined_word` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`);
  
ALTER TABLE `combined`
  ADD CONSTRAINT `fk_combined_url` FOREIGN KEY (`url_id`) REFERENCES `urls` (`id`);

SET FOREIGN_KEY_CHECKS=1;
