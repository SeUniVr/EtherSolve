package decompiler;

import java.util.List;

/**
 * Creates a connection with instructions by mapping:
 *  offset, nextOffset to visit, content.
 */
public class BlockTracing {
    private Long currentOffset;
    private Long successorOffset;
    private StringBuilder code;

    public BlockTracing(Long currentOffset, Long successorOffset, StringBuilder code) {
        this.currentOffset = currentOffset;
        this.successorOffset = successorOffset;
        this.code = code;
    }

    public Long getCurrentOffset() { return currentOffset; }
    public Long getSuccessorOffset() { return successorOffset; }
    public StringBuilder getCode() { return code; }

    public void setCurrentOffset(Long currentOffset) { this.currentOffset = currentOffset; }
    public void setSuccessorOffset(Long successorOffset) {
        this.successorOffset = successorOffset;
    }

    /**
     * Append code to this BlockTrace
     * @param snippet -> code to append
     */
    public void appendCode(String snippet) {
        code.append(snippet);
    }

    /**
     * Get a specific BlockTrace by its current Offset
     * @param relations
     * @param offset
     * @return BlockTrace if exists, null insted.
     */
    public BlockTracing findRelation(List<BlockTracing> relations, Long offset) {
        for (BlockTracing bl : relations) {{
            if (bl.getCurrentOffset().equals(offset)) {
                return bl;
            }
        }}
        return null;
    }

    @Override
    public String toString() {
        return  + currentOffset + ", " + successorOffset + ", " + code;
    }

}
