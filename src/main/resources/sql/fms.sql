-- phpMyAdmin SQL Dump
-- version 4.6.6deb5ubuntu0.5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: 2021-05-13 09:28:42
-- 服务器版本： 5.7.33-0ubuntu0.18.04.1
-- PHP Version: 7.2.24-0ubuntu0.18.04.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `FMS`
--

-- --------------------------------------------------------

--
-- 表的结构 `userFiles`
--

CREATE TABLE `userFiles` (
  `id` int(8) NOT NULL,
  `oldFileName` varchar(200) NOT NULL,
  `newFileName` varchar(300) NOT NULL,
  `ext` varchar(20) NOT NULL,
  `path` varchar(300) NOT NULL,
  `size` varchar(200) NOT NULL,
  `type` varchar(100) NOT NULL,
  `isImg` varchar(8) NOT NULL,
  `downCounts` varchar(6) NOT NULL,
  `uploadTime` date NOT NULL,
  `userId` int(8) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
  `id` int(8) NOT NULL,
  `username` int(30) NOT NULL,
  `password` int(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- 转存表中的数据 `user`
--

INSERT INTO `user` (`id`, `username`, `password`) VALUES
(1, 2018212194, 2018212194),
(2, 2018212197, 2018212197);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `userFiles`
--
ALTER TABLE `userFiles`
  ADD PRIMARY KEY (`id`),
  ADD KEY `userId` (`userId`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- 限制导出的表
--

--
-- 限制表 `userFiles`
--
ALTER TABLE `userFiles`
  ADD CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `user` (`id`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
