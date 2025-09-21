package decompiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnownHashEvent {
    private final String hash;
    private final String signature;
    private final int indexCount;
    private final List<String> nameArgs;
    private final List<String> typeArgs;

    public static final List<KnownHashEvent> KNOWN_HASH_EVENTS = new ArrayList<>();

    public KnownHashEvent(String hash, String signature, int indexCount, List<String> typeArgs, List<String> nameArgs) {
        this.hash = hash;
        this.signature = signature;
        this.indexCount = indexCount;
        this.typeArgs = typeArgs;
        this.nameArgs = nameArgs;
    }

    public static void init() {
        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0x7fcf532c15f0a6db0bd6d0e038bea71d30d808c7d98cb3bf7268a95bf5081b65",
                "Withdrawal",
                2,
                Arrays.asList("address", "uint"),
                Arrays.asList("receiver", "amount")
        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0xe1fffcc4923d04b559f4d29a8bfc6cda04eb5b0d3c460751c2402c5c5cc9109c",
                "Deposit",
                2,
                Arrays.asList("address", "uint"),
                Arrays.asList("sender", "amount")
        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0xa9ef596c73fbcfb4a78bc139b1a19c35247c1f37d2489c0513ad2dd3eb7d8c6b",

                "NewProposal",
                2,
                Arrays.asList("uint", "string"),
                Arrays.asList("proposalId", "name")
        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0x4d99b957a2bc29a30ebd96a7be8e68fe50a3c701db28a91436490b7d53870ca4",
                "Voted",
                2,
                Arrays.asList("address", "uint"),
                Arrays.asList("voter", "proposalId")
        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0xafbb5e6416c3bf0bd3cecb8a08b828891b0ae3613b291d4d3a18864c80049070",
                "InterestSet",
                1,
                Arrays.asList("uint"),
                Arrays.asList("a")
        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                "Transfer",
                3,
                Arrays.asList("address", "address", "uint"),
                Arrays.asList("from", "to", "value")

        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0xc0ba8fe4b176c1714197d43b9cc6bcf797a4a7461c5fe8d0ef6e184ae7601e51",
                "Submission",
                1,
                Arrays.asList("uint"),
                Arrays.asList("transactionId")

        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0x4a504a94899432a9846e1aa406dceb1bcfd538bb839071d49d1e5e23f5be30ef",
                "Confirmation",
                2,
                Arrays.asList("address", "uint"),
                Arrays.asList("owner", "transactionId")

        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0x33e13ecb54c3076d8e8bb8c2881800a4d972b792045ffae98fdf46df365fed75",
                "Execution",
                1,
                Arrays.asList("uint"),
                Arrays.asList("transactionId")

        ));

        KNOWN_HASH_EVENTS.add(new KnownHashEvent(
                "0x3f784155aa459466ce143eb76c376834c30b26979da03bd4be74cd146c4bb1c3",
                "MessageUpdated",
                2,
                Arrays.asList("address", "string"),
                Arrays.asList("_sender", "_newMessage")
        ));
    }

    public String getHash() { return hash; }
    public String getSignature() { return signature; }
    public int getIndexCount() { return indexCount; }
    public List<String> getTypeArgs() { return typeArgs; }
    public List<String> getNameArgs() { return nameArgs; }
}

