
# Rayzo Audit Layer – Backend Integration Guide

This document explains how the backend system (built with **Spring Boot**) should interact with the deployed smart contract on the **Solana Devnet** built using **Anchor Framework** on the **Solana**.

The contract stores AI-generated report metadata on-chain to create a **tamper-proof audit record**.

---

# 1. Smart Contract Information

Backend needs the following information to interact with the contract.

### Program ID

```text
FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb
```

### Network

```text
Solana Devnet
```

### RPC Endpoint

```text
https://api.devnet.solana.com
```

### Explorer Link

```text
https://explorer.solana.com/address/FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb?cluster=devnet
```

---

# 2. Files Provided to Backend

The backend team must receive the following files and details.

| Item                   | Description                    |
| ---------------------- | ------------------------------ |
| Program ID             | Smart contract address         |
| IDL file               | Contract interface description |
| Wallet keypair         | Signing wallet                 |
| RPC endpoint           | Devnet connection              |
| Function specification | Parameters required            |

### IDL File

Provide this file from the project:

```text
target/idl/rayzo_auditlayer.json
```

This file defines:

* program methods
* account structure
* parameter types

---

# 3. Wallet Configuration

Transactions must be signed using a Solana wallet.

### Wallet Keypair Path

```text
~/.config/solana/id.json
```

Example usage in backend:

```java
Account wallet = new Account(new File("/home/mozai/.config/solana/id.json"));
```

⚠️ This file contains the private key and must never be exposed publicly.

---

# 4. Smart Contract Function

The backend interacts with the following function.

### Function Name

```text
submit_report
```

### Parameters

| Parameter        | Type    | Description                                 |
| ---------------- | ------- | ------------------------------------------- |
| max_probability  | float   | Highest disease probability predicted by AI |
| confidence_score | float   | AI model confidence                         |
| confidence_level | string  | Low / Medium / High                         |
| priority         | string  | LOW / MEDIUM / HIGH                         |
| triage_score     | integer | Urgency score                               |
| report_hash      | string  | SHA256 hash of AI report                    |

---

# 5. Required Accounts

Each transaction must include the following accounts.

| Account        | Purpose                         |
| -------------- | ------------------------------- |
| report         | New account storing report data |
| user           | Wallet signer                   |
| system_program | Solana system program           |

Example structure:

```javascript
.accounts({
 report: reportAccount.publicKey,
 user: wallet.publicKey,
 systemProgram: SystemProgram.programId
})
```

---

# 6. Backend Integration Steps (Spring Boot)

Backend developers should follow these steps.

---

## Step 1 – Create Spring Boot Project

Create a new project with dependencies:

* Spring Web
* Lombok
* Jackson

Project structure:

```text
backend
 ├── controller
 ├── service
 ├── config
 ├── model
 └── util
```

---

## Step 2 – Add Solana Java Library

Add dependency in `pom.xml`.

```xml
<dependency>
 <groupId>org.p2p</groupId>
 <artifactId>solanaj</artifactId>
 <version>1.17.2</version>
</dependency>
```

This allows the backend to communicate with the Solana blockchain.

---

## Step 3 – Configure RPC Connection

```java
RpcClient client = new RpcClient("https://api.devnet.solana.com");
```

---

## Step 4 – Create AI Report Model

```java
public class AIReport {

 private float maxProbability;
 private float confidenceScore;
 private String confidenceLevel;
 private String priority;
 private int triageScore;

}
```

---

## Step 5 – Generate Report Hash

Before sending data to the blockchain, the backend must hash the report.

Example:

```java
MessageDigest digest = MessageDigest.getInstance("SHA-256");

byte[] hash = digest.digest(jsonString.getBytes());

String reportHash = Hex.encodeHexString(hash);
```

---

## Step 6 – Create Backend API

Example endpoint:

```java
@PostMapping("/submit-report")
public String submitReport(@RequestBody AIReport report) {

 String json = objectMapper.writeValueAsString(report);

 String hash = HashUtil.generateHash(json);

 return solanaService.submitReport(report, hash);
}
```

---

## Step 7 – Send Transaction to Blockchain

Backend service sends transaction:

```java
PublicKey programId =
 new PublicKey("FcsZDye6x3AAWheYgvBrz7MKTzx637M4MiVugrykcAcb");

Transaction transaction = new Transaction();

String signature =
 rpcClient.getApi().sendTransaction(transaction, wallet);
```

---

# 7. Example API Request

```http
POST /submit-report
```

Request body:

```json
{
 "maxProbability":0.001,
 "confidenceScore":0.001,
 "confidenceLevel":"Low",
 "priority":"LOW",
 "triageScore":0
}
```

---

# 8. Blockchain Response

Backend returns the transaction signature.

Example:

```text
5oPKADD5iGfQma1MD5245j4KxQMardXhLKnc6MyS6kyXmFNimeWx1V668M1DfaRMpWYWFeQQfpcG8tr6a94HtMHy
```

This can be verified on the Solana Explorer.

---

# 9. Data Storage Strategy

Best practice architecture:

| Data           | Storage           |
| -------------- | ----------------- |
| Full AI report | Database / IPFS   |
| Report hash    | Solana blockchain |

---

# 10. System Architecture

```text
X-Ray Image
     ↓
AI Model
     ↓
JSON Diagnosis
     ↓
Spring Boot Backend
     ↓
Generate SHA256 hash
     ↓
Solana Smart Contract
     ↓
Immutable Medical Audit Record
```

---

# 11. Security Recommendations

* Never expose wallet private keys
* Validate AI results before submission
* Store only hashes on-chain
* Keep large medical data off-chain

---

# Deliverables Summary

Backend team must receive:

1. Program ID
2. IDL file
3. Wallet keypair
4. Devnet RPC endpoint
5. Function parameters
6. Integration steps

---




