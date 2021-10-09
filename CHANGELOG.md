# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2021-10-08
### Added
- EEG raw data viewer
- event record what time receive it
### Changed
- move all file to com.flyn.eeg_receiver folder
- MindSetReceiver class renamed EEGReceiver class

## [0.2.2] - 2021-10-01
### Fixed
- dataDecode throw the null pointer exception

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