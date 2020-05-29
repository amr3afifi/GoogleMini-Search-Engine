SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";

CREATE DATABASE IF NOT EXISTS `google` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `google`;


--
-- Table structure for table `images`
--
DROP TABLE IF EXISTS `images`;
CREATE TABLE `google`.`images` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `url_id` INT NOT NULL ,
 `src` TEXT NOT NULL,
 `alt` TEXT ,
 PRIMARY KEY (`id`),
 KEY `url_id` (`url_id`)
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
 PRIMARY KEY (`id`),
 KEY `url_id` (`url_id`),
  KEY `word_id` (`word_id`)
 ) ENGINE = InnoDB AUTO_INCREMENT=1 ;
-- --------------------------------------------------------\


-- --------------------------------------------------------
--
-- Table structure for table `urls`
--
DROP TABLE IF EXISTS `urls`;
CREATE TABLE `google`.`urls` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `url` TEXT NOT NULL ,
 `popularity` DOUBLE NULL DEFAULT '0.0' ,
 `out_going` INT NOT NULL DEFAULT '0' ,
 `in_going` INT NOT NULL DEFAULT '0' ,
 `enter` BOOLEAN NOT NULL DEFAULT FALSE,
 `resume` BOOLEAN NOT NULL DEFAULT FALSE,
 `indexed` BOOLEAN NOT NULL DEFAULT FALSE,
 `max_count` INT NOT NULL DEFAULT '0' ,
 `word_count` INT NOT NULL DEFAULT '0' ,
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


-- --------------------------------------------------------
--
-- Table structure for table `queries`
--
DROP TABLE IF EXISTS `queries`;
CREATE TABLE `google`.`queries` (
 `id` INT NOT NULL AUTO_INCREMENT ,
 `text` TEXT NOT NULL ,
 `location` VARCHAR(100) NOT NULL ,
 `count` INT DEFAULT 1 ,
 `name` VARCHAR(100) ,
 PRIMARY KEY (`id`)
 ) ENGINE = InnoDB AUTO_INCREMENT=1;
-- --------------------------------------------------------

--
-- Constraints for table `combined`
--
ALTER TABLE `combined`
  ADD CONSTRAINT `fk_combined_word` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`);
  
ALTER TABLE `combined`
  ADD CONSTRAINT `fk_combined_url` FOREIGN KEY (`url_id`) REFERENCES `urls` (`id`);

ALTER TABLE `images`
  ADD CONSTRAINT `fk_combined_urlimage` FOREIGN KEY (`url_id`) REFERENCES `urls` (`id`);

SET FOREIGN_KEY_CHECKS=1;

