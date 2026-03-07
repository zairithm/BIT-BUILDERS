package com.Bit_Builder.x_ray.app.Services;

import com.Bit_Builder.x_ray.app.entity.AiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.tx.RawTransactionManager;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

@Service
public class BlockchainService {

    @Value("${blockchain.rpc-url}")
    private String rpcUrl;

    @Value("${blockchain.private-key}")
    private String privateKey;

    @Value("${blockchain.contract-address}")
    private String contractAddress;

    public String logReportToBlockchain(String reportId, AiResult aiResult) {
        try {
            // 1. connect to blockchain
            Web3j web3j = Web3j.build(new HttpService(rpcUrl));

            // 2. load wallet credentials
            Credentials credentials = Credentials.create(privateKey);

            // 3. generate report hash from reportId
            byte[] hashBytes = MessageDigest.getInstance("SHA-256")
                    .digest(reportId.getBytes(StandardCharsets.UTF_8));
            byte[] bytes32 = new byte[32];
            System.arraycopy(hashBytes, 0, bytes32, 0, 32);

            // 4. convert maxProbability to uint256 (multiply by 1000 to avoid decimals)
            BigInteger maxProbability = BigInteger.valueOf(
                    (long)(aiResult.getMax_probability() * 1000));

            // 5. encode function call
            Function function = new Function(
                    "logReport",
                    Arrays.asList(
                            new Bytes32(bytes32),
                            new Uint256(maxProbability),
                            new Utf8String(aiResult.getConfidence_level()),
                            new Utf8String(aiResult.getPriority())
                    ),
                    List.of()
            );

            String encodedFunction = FunctionEncoder.encode(function);

            // 6. send transaction
            RawTransactionManager txManager = new RawTransactionManager(
                    web3j, credentials, 80002L); // 80002 = Polygon Amoy chain ID

            EthSendTransaction txResponse = txManager.sendTransaction(
                    DefaultGasProvider.GAS_PRICE,
                    DefaultGasProvider.GAS_LIMIT,
                    contractAddress,
                    encodedFunction,
                    BigInteger.ZERO
            );

            // 7. return transaction hash
            String txHash = txResponse.getTransactionHash();
            web3j.shutdown();
            return txHash;

        } catch (Exception e) {
            // don't fail the whole upload if blockchain fails
            return "blockchain-error: " + e.getMessage();
        }
    }
}