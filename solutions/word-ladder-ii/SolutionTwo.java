class SolutionTwo {
    private class WordTree {
        private String word;
        private boolean matched;
        private int depth;
        private List<WordTree> children;

        public WordTree(String word, int depth) {
            this.word = word;
            this.depth = depth;
            this.matched = false;
            this.children = new ArrayList();
        }

        public WordTree addChild(String word) {
            WordTree child = new WordTree(word, this.depth + 1);
            this.children.add(child);
            return child;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }
    }

    public List<List<String>> findLadders(String beginWord, String endWord, List<String> wordList) {
        int distance = letterDiffer(beginWord, endWord);
        int endIdx = wordList.indexOf(endWord);

        if (endIdx < 0) {
            return new ArrayList<>();
        } else if (distance == 1) {
            return Arrays.asList(Arrays.asList(beginWord, endWord));
        }

        List<WordTree> nodeList = new ArrayList<>();

        for (String word : wordList) {
            if (!word.equals(endWord) && !word.equals(beginWord)) {
                nodeList.add(new WordTree(word, Integer.MAX_VALUE));
            }
        }

        Queue<WordTree> queue = new LinkedList<>();
        WordTree root = new WordTree(beginWord, 0);
        int shortest = Integer.MAX_VALUE;

        queue.add(root);

        while (!queue.isEmpty()) {
            WordTree node = queue.poll();
            if (node.depth >= distance - 1 && letterDiffer(endWord, node.word) == 1) {
                shortest = Math.min(node.depth + 1, shortest);
                node.addChild(endWord).setMatched(true);
            }
            for (WordTree newNode : nodeList) {
                if (letterDiffer(node.word, newNode.word) == 1 && newNode.depth > node.depth) {
                    WordTree child = node.addChild(newNode.word);
                    newNode.setDepth(child.depth);
                    queue.add(child);
                } else if (newNode.matched && newNode.depth <= node.depth) {
                    break;
                }
            }
        }
        
        
        return getAllMatchedRoute(root, shortest, null, null);
    }

    private int letterDiffer(String base, String target) {
        int diffCount = 0;
        for (int i = 0; i < base.length(); i++) {
            diffCount += base.charAt(i) != target.charAt(i) ? 1 : 0;
        }
        return diffCount;
    }

    private List<List<String>> getAllMatchedRoute(WordTree root, int shortest, List<String> oneRoute, List<List<String>> allRoutes) {
        if (oneRoute == null || allRoutes == null) {
            allRoutes = new ArrayList();
            oneRoute = new ArrayList();
        }
        if (root != null) {
            oneRoute.add(root.word);
            if (root.matched && root.depth == shortest) {
                allRoutes.add(new ArrayList<>(oneRoute));
            }

            for (WordTree child : root.children) {
                getAllMatchedRoute(child, shortest, oneRoute, allRoutes);
            }

            oneRoute.remove(oneRoute.size() - 1);
        }
        
        return allRoutes;
    }
}