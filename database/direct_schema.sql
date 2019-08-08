-- MySQL dump 10.13  Distrib 5.5.53, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: direct
-- ------------------------------------------------------
-- Server version	5.5.53-0ubuntu0.12.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `direct`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `direct` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `direct`;

--
-- Table structure for table `CCDAValidationReport`
--

DROP TABLE IF EXISTS `CCDAValidationReport`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CCDAValidationReport` (
  `CCDAValidationReportID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `Filename` varchar(255) DEFAULT NULL,
  `ValidationReport` longblob,
  PRIMARY KEY (`CCDAValidationReportID`),
  KEY `LogID` (`LogID`),
  CONSTRAINT `CCDAValidationReport_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ContactEmail`
--

DROP TABLE IF EXISTS `ContactEmail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ContactEmail` (
  `ContactEmailID` varchar(255) NOT NULL,
  `DirectEmailID` varchar(255) NOT NULL,
  `ContactEmail` varchar(255) NOT NULL,
  PRIMARY KEY (`ContactEmailID`),
  KEY `DirectEmailID` (`DirectEmailID`),
  CONSTRAINT `ContactEmail_ibfk_1` FOREIGN KEY (`DirectEmailID`) REFERENCES `DirectEmail` (`DirectEmailID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Detail`
--

DROP TABLE IF EXISTS `Detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Detail` (
  `DetailID` varchar(255) NOT NULL,
  `PartID` varchar(255) NOT NULL,
  `Counter` int(11) DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `DTS` varchar(255) DEFAULT NULL,
  `Found` text,
  `Expected` text,
  `RFC` text,
  PRIMARY KEY (`DetailID`),
  KEY `PartID` (`PartID`),
  CONSTRAINT `Detail_ibfk_1` FOREIGN KEY (`PartID`) REFERENCES `Part` (`PartID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DirectEmail`
--

DROP TABLE IF EXISTS `DirectEmail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DirectEmail` (
  `DirectEmailID` varchar(255) NOT NULL,
  `DirectEmail` varchar(255) NOT NULL,
  PRIMARY KEY (`DirectEmailID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FromLine`
--

DROP TABLE IF EXISTS `FromLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FromLine` (
  `FromLineID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `FromLine` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`FromLineID`),
  KEY `LogID` (`LogID`),
  KEY `FromLine` (`FromLine`),
  CONSTRAINT `FromLine_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Log`
--

DROP TABLE IF EXISTS `Log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Log` (
  `LogID` varchar(255) NOT NULL,
  `Incoming` tinyint(4) NOT NULL,
  `Timestamp` varchar(255) NOT NULL,
  `Status` tinyint(4) DEFAULT NULL,
  `Mdn` tinyint(4) DEFAULT NULL,
  `OrigDate` varchar(255) DEFAULT NULL,
  `MessageId` varchar(255) DEFAULT NULL,
  `OriginalMessageId` varchar(255) DEFAULT NULL,
  `MIMEVersion` varchar(255) DEFAULT NULL,
  `Subject` varchar(255) DEFAULT NULL,
  `ContentType` text,
  `ContentDisposition` text,
  PRIMARY KEY (`LogID`),
  UNIQUE KEY `MessageId` (`MessageId`),
  KEY `LogMessageId` (`MessageId`(64))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Part`
--

DROP TABLE IF EXISTS `Part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Part` (
  `PartID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `RawMessage` longblob,
  `ContentType` text,
  `ContentTransferEncoding` varchar(255) DEFAULT NULL,
  `ContentDisposition` varchar(255) DEFAULT NULL,
  `Status` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`PartID`),
  KEY `LogID` (`LogID`),
  CONSTRAINT `Part_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PartRelationship`
--

DROP TABLE IF EXISTS `PartRelationship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `PartRelationship` (
  `PartRelationshipID` varchar(255) NOT NULL,
  `ParentID` varchar(255) DEFAULT NULL,
  `ChildID` varchar(255) NOT NULL,
  PRIMARY KEY (`PartRelationshipID`),
  KEY `ParentID` (`ParentID`),
  KEY `ChildID` (`ChildID`),
  CONSTRAINT `PartRelationship_ibfk_1` FOREIGN KEY (`ParentID`) REFERENCES `Part` (`PartID`),
  CONSTRAINT `PartRelationship_ibfk_2` FOREIGN KEY (`ChildID`) REFERENCES `Part` (`PartID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Received`
--

DROP TABLE IF EXISTS `Received`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Received` (
  `ReceivedID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `Received` text,
  PRIMARY KEY (`ReceivedID`),
  KEY `LogID` (`LogID`),
  CONSTRAINT `Received_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReplyTo`
--

DROP TABLE IF EXISTS `ReplyTo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReplyTo` (
  `ReplyToID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `ReplyTo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ReplyToID`),
  KEY `LogID` (`LogID`),
  CONSTRAINT `ReplyTo_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SmtpEdgeContent`
--

DROP TABLE IF EXISTS `SmtpEdgeContent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SmtpEdgeContent` (
  `SmtpEdgeContentID` varchar(255) NOT NULL,
  `SmtpEdgeLogID` varchar(255) NOT NULL,
  `Content` longblob,
  PRIMARY KEY (`SmtpEdgeContentID`),
  KEY `SmtpEdgeLogID` (`SmtpEdgeLogID`),
  CONSTRAINT `SmtpEdgeContent_ibfk_1` FOREIGN KEY (`SmtpEdgeLogID`) REFERENCES `SmtpEdgeLog` (`SmtpEdgeLogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SmtpEdgeLog`
--

DROP TABLE IF EXISTS `SmtpEdgeLog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SmtpEdgeLog` (
  `SmtpEdgeLogID` varchar(255) NOT NULL,
  `SmtpEdgeProfileID` varchar(255) NOT NULL,
  `Timestamp` varchar(255) NOT NULL,
  `TransactionID` varchar(255) DEFAULT NULL,
  `TestCaseNumber` varchar(50) DEFAULT NULL,
  `CriteriaMet` tinyint(4) DEFAULT NULL,
  `TestRequestsResponse` text,
  PRIMARY KEY (`SmtpEdgeLogID`),
  KEY `SmtpEdgeProfileID` (`SmtpEdgeProfileID`),
  KEY `SmtpEdgeLogTransaction` (`TransactionID`(64)),
  KEY `SmtpEdgeLogTimestamp` (`Timestamp`(64)),
  CONSTRAINT `SmtpEdgeLog_ibfk_1` FOREIGN KEY (`SmtpEdgeProfileID`) REFERENCES `SmtpEdgeProfile` (`SmtpEdgeProfileID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SmtpEdgeLog_bk_04302018`
--

DROP TABLE IF EXISTS `SmtpEdgeLog_bk_04302018`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SmtpEdgeLog_bk_04302018` (
  `SmtpEdgeLogID` varchar(255) NOT NULL,
  `SmtpEdgeProfileID` varchar(255) NOT NULL,
  `Timestamp` varchar(255) NOT NULL,
  `TransactionID` varchar(255) DEFAULT NULL,
  `TestCaseNumber` varchar(50) DEFAULT NULL,
  `CriteriaMet` tinyint(4) DEFAULT NULL,
  `TestRequestsResponse` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SmtpEdgeProfile`
--

DROP TABLE IF EXISTS `SmtpEdgeProfile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SmtpEdgeProfile` (
  `SmtpEdgeProfileID` varchar(255) NOT NULL,
  `Username` varchar(255) NOT NULL,
  `ProfileName` varchar(255) DEFAULT NULL,
  `SUTSMTPAddress` varchar(255) DEFAULT NULL,
  `SUTEmailAddress` varchar(255) DEFAULT NULL,
  `SUTUsername` varchar(255) DEFAULT NULL,
  `SUTPassword` varbinary(255) DEFAULT NULL,
  `useTLS` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`SmtpEdgeProfileID`),
  UNIQUE KEY `Username` (`Username`,`ProfileName`),
  CONSTRAINT `SmtpEdgeProfile_ibfk_1` FOREIGN KEY (`Username`) REFERENCES `Users` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `SmtpEdgeProfile_bk_04302018`
--

DROP TABLE IF EXISTS `SmtpEdgeProfile_bk_04302018`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SmtpEdgeProfile_bk_04302018` (
  `SmtpEdgeProfileID` varchar(255) NOT NULL,
  `Username` varchar(255) NOT NULL,
  `ProfileName` varchar(255) DEFAULT NULL,
  `SUTSMTPAddress` varchar(255) DEFAULT NULL,
  `SUTEmailAddress` varchar(255) DEFAULT NULL,
  `SUTUsername` varchar(255) DEFAULT NULL,
  `SUTPassword` varbinary(255) DEFAULT NULL,
  `useTLS` tinyint(1) DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ToLine`
--

DROP TABLE IF EXISTS `ToLine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ToLine` (
  `ToLineID` varchar(255) NOT NULL,
  `LogID` varchar(255) NOT NULL,
  `ToLine` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ToLineID`),
  KEY `LogID` (`LogID`),
  KEY `ToLine` (`ToLine`),
  CONSTRAINT `ToLine_ibfk_1` FOREIGN KEY (`LogID`) REFERENCES `Log` (`LogID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UserDirect`
--

DROP TABLE IF EXISTS `UserDirect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserDirect` (
  `Username` varchar(255) NOT NULL,
  `DirectEmailID` varchar(255) NOT NULL,
  PRIMARY KEY (`Username`,`DirectEmailID`),
  KEY `DirectEmailID` (`DirectEmailID`),
  CONSTRAINT `UserDirect_ibfk_1` FOREIGN KEY (`DirectEmailID`) REFERENCES `DirectEmail` (`DirectEmailID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `Username` varchar(255) NOT NULL,
  `Password` varchar(255) NOT NULL,
  PRIMARY KEY (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XDRRecord`
--

DROP TABLE IF EXISTS `XDRRecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XDRRecord` (
  `XDRRecordID` varchar(255) NOT NULL,
  `Username` varchar(255) DEFAULT NULL,
  `TestCaseNumber` varchar(8) DEFAULT NULL,
  `Timestamp` varchar(255) NOT NULL,
  `CriteriaMet` tinyint(4) DEFAULT NULL,
  `MDHTValidationReport` longblob,
  PRIMARY KEY (`XDRRecordID`),
  KEY `Username` (`Username`),
  CONSTRAINT `XDRRecord_ibfk_1` FOREIGN KEY (`Username`) REFERENCES `Users` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XDRReportItem`
--

DROP TABLE IF EXISTS `XDRReportItem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XDRReportItem` (
  `XDRReportItemID` varchar(255) NOT NULL,
  `XDRTestStepID` varchar(255) NOT NULL,
  `Report` mediumblob,
  `ReportType` int(11) DEFAULT NULL,
  PRIMARY KEY (`XDRReportItemID`),
  KEY `XDRTestStepID` (`XDRTestStepID`),
  CONSTRAINT `XDRReportItem_ibfk_1` FOREIGN KEY (`XDRTestStepID`) REFERENCES `XDRTestStep` (`XDRTestStepID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XDRSimulator`
--

DROP TABLE IF EXISTS `XDRSimulator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XDRSimulator` (
  `XDRSimulatorID` varchar(255) NOT NULL,
  `XDRTestStepID` varchar(255) DEFAULT NULL,
  `SimulatorId` varchar(255) DEFAULT NULL,
  `Endpoint` varchar(255) DEFAULT NULL,
  `EndpointTLS` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XDRSimulatorID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XDRTestStep`
--

DROP TABLE IF EXISTS `XDRTestStep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XDRTestStep` (
  `XDRTestStepID` varchar(255) NOT NULL,
  `XDRRecordID` varchar(255) NOT NULL,
  `Timestamp` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `MessageId` varchar(255) DEFAULT NULL,
  `DirectFrom` varchar(255) DEFAULT NULL,
  `Hostname` varchar(255) DEFAULT NULL,
  `CriteriaMet` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`XDRTestStepID`),
  UNIQUE KEY `MessageId` (`MessageId`),
  KEY `XDRRecordID` (`XDRRecordID`),
  KEY `XDRTestStepMessageId` (`MessageId`(64)),
  KEY `XDRTestStepHostname` (`Hostname`(64)),
  CONSTRAINT `XDRTestStep_ibfk_1` FOREIGN KEY (`XDRRecordID`) REFERENCES `XDRRecord` (`XDRRecordID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `XDRVanilla`
--

DROP TABLE IF EXISTS `XDRVanilla`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `XDRVanilla` (
  `XDRVanillaID` varchar(255) NOT NULL,
  `Request` mediumblob,
  `Response` mediumblob,
  `SamlReport` blob,
  `SimId` varchar(255) DEFAULT NULL,
  `Timestamp` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`XDRVanillaID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-01-16 19:35:13
