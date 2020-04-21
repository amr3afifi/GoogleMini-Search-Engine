
SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = serverTimezone;


CREATE DATABASE IF NOT EXISTS `Database` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `Database`;

-- --------------------------------------------------------
--
-- Table structure for table `urls`
--

DROP TABLE IF EXISTS `urls`;
CREATE TABLE `urls` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(200) unsigned NOT NULL,
  `popularity` double(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `words`
--

DROP TABLE IF EXISTS `words`;
CREATE TABLE `words` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `word` varchar(30) unsigned NOT NULL,
  `ts` int(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `combined`
--

DROP TABLE IF EXISTS `combined`;
CREATE TABLE `combined` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `url_id` int(10) unsigned NOT NULL,
  `word_id` int(10) unsigned NOT NULL,
  `importance` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`)
  KEY `url_id` (`url_id`)
  KEY `word_id` (`word_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------\

--
-- Constraints for table `combined`
--
ALTER TABLE `combined`
  ADD CONSTRAINT `fk_combined_di` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`);
  ADD CONSTRAINT `fk_combined_di` FOREIGN KEY (`url_id`) REFERENCES `urls` (`id`);

SET FOREIGN_KEY_CHECKS=1;

