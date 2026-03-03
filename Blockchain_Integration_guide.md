# 🔗 Rayzo Audit Blockchain Integration Guide

---
### 1️⃣ Network Details

* Network: Polygon Amoy Testnet
* Chain ID: 80002
* RPC URL: https://rpc-amoy.polygon.technology
* Currency: MATIC

---

### 2️⃣ Smart Contract Details

* Contract Name: `RayzoAuditLayer`
* Contract Address:
  `0xf1aC91da59d8926401188Eb21F43e3614BDE4Ef2`

---

### 3️⃣ ABI Location

```
Rayzo_Blockchain/artifacts/contracts/RayzoAuditLayer.sol/RayzoAuditLayer.json
```

Use the `"abi"` section from this file.

---

### 4️⃣ Role-Based Access

Contract uses OpenZeppelin AccessControl.

#### Roles:

* DEFAULT_ADMIN_ROLE → deployer
* HOSPITAL_ROLE → can log reports

Before calling `logReport`, hospital address must have HOSPITAL_ROLE.

---

### 5️⃣ Core Function

```solidity
function logReport(
    bytes32 reportHash,
    uint256 maxProbability,
    string memory confidenceLevel,
    string memory priority
)
```

---

### 6️⃣ Backend Implementation Flow

1. Backend generates medical report.
2. Backend creates hash:

```js
ethers.keccak256(
  ethers.toUtf8Bytes("REPORT_001")
)
```

3. Backend calls `logReport()`
4. Transaction hash stored in DB.
5. On-chain event `ReportLogged` emitted.

---

### 7️⃣ Event Emitted

```solidity
event ReportLogged(
  uint256 reportId,
  bytes32 reportHash,
  uint256 maxProbability,
  string confidenceLevel,
  string priority,
  uint256 timestamp,
  address submittedBy
);
```

---

### 8️⃣ Security Notes

* Private key must be stored in `.env`
* Never expose private key in frontend
* Only backend signs transactions
* Consider using a multisig in production

---

# 📚 Official Documentation Links 


* Ethers.js Docs
  [https://docs.ethers.org/](https://docs.ethers.org/)

* Hardhat Docs
  [https://hardhat.org/docs](https://hardhat.org/docs)

* OpenZeppelin AccessControl
  [https://docs.openzeppelin.com/contracts/4.x/access-control](https://docs.openzeppelin.com/contracts/4.x/access-control)

* Polygon Amoy Network
  [https://polygon.technology/](https://polygon.technology/)

---

