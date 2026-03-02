const hre = require("hardhat");

async function main() {

  const contractAddress = "0xf1aC91da59d8926401188Eb21F43e3614BDE4Ef2";

  const Rayzo = await hre.ethers.getContractAt(
    "RayzoAuditLayer",
    contractAddress
  );

  const reportHash = hre.ethers.keccak256(
    hre.ethers.toUtf8Bytes("REPORT_001")
  );

  const tx = await Rayzo.logReport(
    reportHash,
    95,                 // maxProbability
    "HIGH_CONFIDENCE",  // confidenceLevel
    "CRITICAL"          // priority
  );

  await tx.wait();

  console.log("Report logged successfully!");
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});