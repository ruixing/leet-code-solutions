public class Solution {
    public int[] findRedundantDirectedConnection(int[][] edges) {
        int[] result = new int[2];
        Stack<Integer[]> tasks = new Stack();
        
        tasks.push(edges[0]);
        
        while (!tasks.empty()) {
            int[] edge = tasks.pop();
            for (int[] newEdge : edges) {

            }
        }
        
        
        
        return result;
    }
}
