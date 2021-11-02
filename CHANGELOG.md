# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.2] - 2021-10-26
### Changed
- EEG Power event / listener renamed Band event / listener
- csv file add device sent band data
- time unit change to nanoSecond
### Fixed
- csv file's raw data amount fix to 2560

## [0.3.1] - 2021-10-18
### Added
- Save EEG raw data to csv file
- Raw data chart add pause function to halt data update
- Choice box which can select connection COM port

## [0.3.0] - 2021-10-08
### Added
- EEG raw data viewer
- Event record what time receive it
### Changed
- Move all file to com.flyn.eeg_receiver folder
- MindSetReceiver class renamed EEGReceiver class

## [0.2.2] - 2021-10-01
### Fixed
- DataDecode throw the null pointer exception

## [0.2.1] - 2021-09-27
### Added
- DataReceiver, receive device data and decode it
- possible event and listener
### Fixed
- DataReceiver name mistake

## [0.2.0] - 2021-09-22
### Added
- Using fazecast's jSerialComm library communicate with mind wave mobile device
### Changed
- Change kt file locate from java folder to kotlin folder
### Removed
- ThinkGear because thinkgear DLL is 32bits version

## [0.1.0] - 2021-09-15
### Added
- ThinkGear using JNA from thinkgear.dll