import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SolutionOne {
    private class WordTree {
        private String word;
        private boolean matched;
        private int depth;
        private List<WordTree> children;

        public WordTree(String word, int depth) {
            this.word = word;
            this.depth = depth;
            this.matched = false;
            this.children = new ArrayList<>();
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
        int minDepth = letterDiffer(beginWord, endWord) -  1;
        int endIdx = wordList.indexOf(endWord);
        List<List<String>> allRoutes = new ArrayList<>();
        List<String> oneRoute = new ArrayList<>();

        if (endIdx < 0) {
            return allRoutes;
        } else if (minDepth == 0) {
            oneRoute.add(beginWord);
            oneRoute.add(endWord);
            allRoutes.add(oneRoute);
            return allRoutes;
        }

        List<WordTree> nodeList = new ArrayList<>();

        for (String word : wordList) {
            if (!word.equals(endWord) && !word.equals(beginWord)) {
                nodeList.add(new WordTree(word, Integer.MAX_VALUE));
            }
        }

        WordTree root = new WordTree(beginWord, 0);

        buildWordTree(nodeList, beginWord, endWord, root, minDepth);
        
        getAllMatchedRoute(root, oneRoute, allRoutes, beginWord);
        int shortest = Integer.MAX_VALUE;

        for (List<String> route : allRoutes) {
            if (route.size() < shortest) {
                shortest = route.size();
            }
        }

        Iterator<List<String>> routesIterator = allRoutes.iterator();
        while (routesIterator.hasNext()) {
            List<String> route = routesIterator.next();
            if (route.size() > shortest) {
                routesIterator.remove();
            }
        }

        return allRoutes;
    }

    private void buildWordTree(List<WordTree> wordList, String beginWord, String endWord, WordTree root, int minDepth) {
        int randomIndex = (int)Math.floor(Math.random() * wordList.size());
        for (int i = 0; i < wordList.size(); i++) {
            WordTree node = wordList.get((randomIndex + i) % wordList.size());
            if (letterDiffer(root.word, node.word) == 1 && root.depth + 1 <= node.depth) {
                WordTree child = root.addChild(node.word);
                node.setDepth(child.depth);
            } else if (node.matched && root.depth >= node.depth) {
                break;
            }
        }

        for (WordTree child : root.children) {
            if (child.depth >= minDepth && letterDiffer(endWord, child.word) == 1) {
                child.addChild(endWord).setMatched(true);
            } else {
                buildWordTree(wordList, beginWord, endWord, child, minDepth);
            }
        }
    }

    private int letterDiffer(String base, String target) {
        int diffCount = 0;
        for (int i = 0; i < base.length(); i++) {
            diffCount += base.charAt(i) != target.charAt(i) ? 1 : 0;
        }
        return diffCount;
    }

    private void getAllMatchedRoute(WordTree root, List<String> oneRoute, List<List<String>> allRoutes, String head) {
        if (root != null) {
            oneRoute.add(root.word);
            if (root.matched) {
                allRoutes.add(new ArrayList<>(oneRoute));
            }

            for (WordTree child : root.children) {
                getAllMatchedRoute(child, oneRoute, allRoutes, head);
            }

            oneRoute.remove(oneRoute.size() - 1);
        }
    }
}