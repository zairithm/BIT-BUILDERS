const hre = require("hardhat");

async function main() {
  const contractAddress = "0xf1aC91da59d8926401188Eb21F43e3614BDE4Ef2";
  const hospitalAddress = "0xCd992563A820AB1a63251cCC6c1849447c7E9bB5";

  const Rayzo = await hre.ethers.getContractAt(
    "RayzoAuditLayer",
    contractAddress
  );

  const tx = await Rayzo.grantHospitalRole(hospitalAddress);
  await tx.wait();

  console.log("Hospital role granted to:", hospitalAddress);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});