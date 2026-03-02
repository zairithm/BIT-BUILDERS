// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/access/AccessControl.sol";

contract RayzoAuditLayer is AccessControl {

    bytes32 public constant HOSPITAL_ROLE = keccak256("HOSPITAL_ROLE");

    struct Report {
        bytes32 reportHash;
        uint256 maxProbability;
        string confidenceLevel;
        string priority;
        uint256 timestamp;
        address submittedBy;
    }

    mapping(uint256 => Report) public reports;
    uint256 public reportCounter;

    event ReportLogged(
        uint256 reportId,
        bytes32 reportHash,
        uint256 maxProbability,
        string confidenceLevel,
        string priority,
        uint256 timestamp,
        address submittedBy
    );

    constructor() {
        _grantRole(DEFAULT_ADMIN_ROLE, msg.sender);
    }

    function logReport(
        bytes32 _reportHash,
        uint256 _maxProbability,
        string memory _confidenceLevel,
        string memory _priority
    ) external onlyRole(HOSPITAL_ROLE) {

        reportCounter++;

        reports[reportCounter] = Report({
            reportHash: _reportHash,
            maxProbability: _maxProbability,
            confidenceLevel: _confidenceLevel,
            priority: _priority,
            timestamp: block.timestamp,
            submittedBy: msg.sender
        });

        emit ReportLogged(
            reportCounter,
            _reportHash,
            _maxProbability,
            _confidenceLevel,
            _priority,
            block.timestamp,
            msg.sender
        );
    }

    function grantHospitalRole(address hospital)
        external
        onlyRole(DEFAULT_ADMIN_ROLE)
    {
        grantRole(HOSPITAL_ROLE, hospital);
    }
}