package decompiler;

import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;
import parseTree.cfg.Cfg;

import java.util.*;

public class PathsExtractor {

    public PathsExtractor() {}

    /**
     * Creation of all paths of function execution.
     * @param cfg
     * @param entryPoint
     * @return a List of all possible paths.
     */
    public List<List<BasicBlock>> findAllPaths(Cfg cfg, long entryPoint) {
        List<List<BasicBlock>> paths = new ArrayList<>();
        BasicBlock entryBlock = cfg.getBasicBlock(entryPoint);

        if (entryBlock != null) {
            // Counter of visited blocks at Global Level (all paths).
            Set<Long> globallyVisited = new HashSet<>();
            // Use DFS to trace all the paths of the block (recursion).
            DFSBuildPaths(entryBlock, new ArrayList<>(), paths, new HashSet<>(), globallyVisited);
        }
        // Pruning of the shorter path which has the same prefix as another one
        paths = pruneShorterPrefixPath(new ArrayList<>(new LinkedHashSet<>(paths)));
        return paths;
    }

    /**
     * Apply DFS to get all the path in CFG analysis.
     * In this way, it's possible to simulate all branches and multiple executions.
     * @param currentBlock
     * @param currentPath
     * @param paths
     * @param locallyVisited -> blocks which are considere visited for this path
     * @param globallyVisited -> blocks which are considered visited for everyone
     */
    private void DFSBuildPaths(BasicBlock currentBlock, List<BasicBlock> currentPath, List<List<BasicBlock>> paths, Set<Long> locallyVisited, Set<Long> globallyVisited) {
        // Anti-loop condition for current path.
        if (locallyVisited.contains(currentBlock.getOffset())) return;

        // Add block to path and to local visited
        currentPath.add(currentBlock);
        locallyVisited.add(currentBlock.getOffset());

        // If a block is not of type CODE, we don't want to revisit it or to visit its children.
        // We want this condition for all paths, in order to avoid branch, loop condition analysis.
        if (currentBlock.getType() != BasicBlockType.CODE)
            globallyVisited.add(currentBlock.getOffset());


        List<BasicBlock> successors = currentBlock.getSuccessors();
        if (successors.isEmpty())
            // End of path, there aren't successors
            paths.add(new ArrayList<>(currentPath));
        else {
            // Explore children
            for (BasicBlock successor : successors) {
                DFSBuildPaths(successor, new ArrayList<>(currentPath), paths, new HashSet<>(locallyVisited), globallyVisited);
            }
        }
    }

    /**
     * Prune all paths which are shorter than other with same prefix, or are subsequence of longer paths.
     * @param paths
     * @return -> pruned List of paths.
     */
    private List<List<BasicBlock>> pruneShorterPrefixPath(List<List<BasicBlock>> paths) {
        List<List<BasicBlock>> pruned = new ArrayList<>();

        for (List<BasicBlock> path : paths) {
            boolean isRedundant = false;

            for (List<BasicBlock> otherBlocks : paths) {
                if (otherBlocks == path) continue;;

                // prefix check
                if (otherBlocks.size() > path.size() && blockStartsWith(otherBlocks, path)) {
                    isRedundant = true;
                    break;
                }

                // subsequence check (same start and end)
                if (!path.isEmpty() && !otherBlocks.isEmpty() &&
                        path.get(0).equals(otherBlocks.get(0)) &&
                        path.get(path.size() - 1).equals(otherBlocks.get(otherBlocks.size() - 1)) &&
                        isBlockSubsequence(path, otherBlocks)) {
                    isRedundant = true;
                    break;
                }
            }
            if (!isRedundant)
                pruned.add(path);
        }
        return pruned;
    }

    /**
     * Check which path is longer.
     * @param shortPath
     * @param longPath
     * @return
     */
    private boolean isBlockSubsequence(List<BasicBlock> shortPath, List<BasicBlock> longPath) {
        int i = 0, j = 0;
        while (i < shortPath.size() && j < longPath.size()) {
            if (shortPath.get(i).equals(longPath.get(j)))
                i++;
            j++;
        }
        return i == shortPath.size();
    }

    /**
     * Compare prefix of two blocks.
     * @param shortPath
     * @param longPath
     * @return -> if same prefix
     */
    private boolean blockStartsWith(List<BasicBlock> shortPath, List<BasicBlock> longPath) {
        if (shortPath.size() > longPath.size()) return false;
        for (int i = 0; i < shortPath.size(); i++) {
            if (!longPath.get(i).equals(shortPath.get(i))) return false;
        }
        return true;
    }

}
