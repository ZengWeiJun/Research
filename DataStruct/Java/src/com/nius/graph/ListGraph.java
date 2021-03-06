package com.nius.graph;

import com.nius.union_find.UnionFind.GenericUnionFind;

import java.util.*;
import java.util.Map.Entry;

/// 接近邻接表实现方式
public class ListGraph<V, E> extends Graph<V, E> {
    private Map<V, Vertex<V, E>> vertices = new HashMap<>();
    private Set<Edge<V, E>> edges = new HashSet<>();
    private Comparator<Edge<V, E>> edgeComparator = (Edge<V, E> e1, Edge<V, E> e2)->{
        return weightManager.compare(e1.weight, e2.weight);
    };

    public ListGraph() {}
    public ListGraph(WeightManager<E> weightManager) {
        this.weightManager = weightManager;
    }

    private static class Vertex<V, E> {
        // 存储的值
        V value;

        // 这里的边通常不用考虑顺序，因此用set存储效率更高
        // 以改点为终点的边(进来的边)
        Set<Edge<V, E>> inEdges = new HashSet<>();
        // 以该点为起点的边(出去的边)
        Set<Edge<V, E>> outEdges = new HashSet<>();

        public Vertex(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vertex<?, ?> vertex = (Vertex<?, ?>) o;
            // Value 相等视为相等
            return Objects.equals(value, vertex.value);
        }

        @Override
        public int hashCode() {
            // return value == null ? 0: value.hashCode();
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value == null ? "null" : value.toString();
        }
    }
    private static class Edge<V, E> {
        Vertex<V, E> from; // 起点
        Vertex<V, E> to;   // 终点
        E weight;          // 权值

        public Edge(Vertex<V, E> from, Vertex<V, E> to) {
            this.from = from;
            this.to = to;
        }
        EdgeInfo<V, E> info() {
            return new EdgeInfo<>(from.value, to.value, weight);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Edge<?, ?> edge = (Edge<?, ?>) o;
            // from、to相等视为相等
            return Objects.equals(from, edge.from) && Objects.equals(to, edge.to);
        }

        @Override
        public int hashCode() {
            // 如果equals返回true，则hash值必须相等才有意义
            // 上边方法使用from和to属性判断，因此这里也取from和to做计算

            // return from.hashCode() * 31 + to.hashCode();
            return Objects.hash(from, to); // 系统方法，更优
        }

        @Override
        public String toString() {
            return "Edge{" +
                    from +
                    "->" +
                    to +
                    ", weight=" + weight +
                    '}';
        }
    }

    public void print() {
        vertices.forEach((V v, Vertex<V, E> vt)->{
            StringBuilder sb = new StringBuilder();
            sb.append(vt);
            sb.append(": 出[");
            vt.outEdges.forEach((Edge<V, E> e)-> {
                sb.append(e.to + ",");
            });
            if (vt.outEdges.size() > 0) {
                sb.replace(sb.length()-1, sb.length(), "");
            }
            sb.append("]、");
            sb.append("进[");
            vt.inEdges.forEach((Edge<V, E> e)-> {
                sb.append(e.from + ",");
            });
            if (vt.inEdges.size() > 0) {
                sb.replace(sb.length()-1, sb.length(), "");
            }
            sb.append("]");
            System.out.println(sb.toString());
        });
        edges.forEach((Edge<V, E> e)->{
            System.out.println(e);
        });
    }

    @Override
    public int edgeCount() {
        return edges.size();
    }

    @Override
    public int vertexCount() {
        return vertices.size();
    }

    @Override
    public void addVertex(V v) {
        // 此处为判断null，即允许为空
        if (vertices.containsKey(v)) return;
        vertices.put(v, new Vertex<>(v));
    }

    @Override
    public void addEdge(V from, V to) {
        addEdge(from, to, null);
    }

    @Override
    public void addEdge(V from, V to, E weight) {
        // 先判断顶点fron、to是否存在
        Vertex fromVertex = vertices.get(from);
        if (fromVertex == null) {
            fromVertex = new Vertex(from);
            vertices.put(from, fromVertex);
        }

        Vertex toVertex = vertices.get(to);
        if (toVertex == null) {
            toVertex = new Vertex(to);
            vertices.put(to, toVertex);
        }

        // 先看以前是否存在边，如果存在边，更新权值，否则添加一条边
        // 这里可以选择fromVertex.outEdges 或者 toVertex.inEdges
        Edge e = new Edge(fromVertex, toVertex);
        e.weight = weight;
        // 统一先删除以前的边，然后将新的边加进去
        // if (fromVertex.outEdges.contains(e)) { }
        if (fromVertex.outEdges.remove(e)) {
            // 删除成功则说明存在这条边，注意，这里e的比较是比较hash值，不是内存地址
            // 此时，终点的inEdges也许要维护一下
            toVertex.inEdges.remove(e);
            edges.remove(e);
        }

        // 添加边
        fromVertex.outEdges.add(e);
        toVertex.inEdges.add(e);
        edges.add(e);
    }

    @Override
    public void removeVertex(V v) {
        // 如果vertices存在v为key的键值对，删除成功，会返回对应的值，否则删除失败，返回null
        Vertex<V, E> vt = vertices.remove(v);
        if (vt == null) return;

        // 1.删除顶点出去的边
        // 不能一边遍历集合一边删除集合中的东西，这种需求只能使用迭代器
        // 不能使用forEach、for循环，使用迭代器
        for (Iterator<Edge<V, E>> it = vt.outEdges.iterator(); it.hasNext();) {
            Edge<V, E> e = it.next();
            // 维护这一条边两边顶点的出度和入度集合
            // 1).边终点的入度集合
            e.to.inEdges.remove(e);

            // 2).当前点的出度集合(当前集合)
            // 由于这里正在迭代vt.outEdges，不能直接remove，使用迭代器专用删除方法
            //e.from.outEdges.remove(e) == vt.outEdges.remove(e)
            it.remove(); // 效果就是删除当前遍历到的元素

            // 3).维护总集合
            edges.remove(e);
        }

        // 2.删除顶点进来的边(同上)
        for (Iterator<Edge<V, E>> it = vt.inEdges.iterator(); it.hasNext();) {
            Edge<V, E> e = it.next();

            // 1).边起点的出度集合
            e.from.outEdges.remove(e);

            // 2).当前点的入度集合(当前集合)
            it.remove();

            // 3).维护总集合
            edges.remove(e);
        }

    }

    @Override
    public void removeEdge(V from, V to) {
        Vertex fromVertex = vertices.get(from);
        if (fromVertex == null) return;

        Vertex toVertex = vertices.get(to);
        if (toVertex == null) return;

        Edge e = new Edge(fromVertex, toVertex);
        if (e.from.outEdges.remove(e)) {
            e.to.inEdges.remove(e);
            edges.remove(e);
        }
    }

    @Override
    public void bfs(V begin, VertexVisitor<V> visitor) {
        if (visitor == null) return;
        Vertex<V, E> beginVertex = vertices.get(begin);
        if (beginVertex == null) return;

        // 用一个集合记录被遍历过的元素，防止重复遍历
        // java中队列使用LinkedList即可
        Set<Vertex<V, E>> visitedVertices = new HashSet<>();
        Queue<Vertex<V, E>> queue = new LinkedList<>();
        queue.offer(beginVertex);
        visitedVertices.add(beginVertex);

        while (!queue.isEmpty()) {
            Vertex<V, E> vertex = queue.poll();
            if (visitor.visit(vertex.value)) return;

            // 将出度集合入队
            for (Edge<V, E> e : vertex.outEdges) {
                if (visitedVertices.contains(e.to)) continue;
                queue.offer(e.to);
                visitedVertices.add(e.to);
            }
        }
    }

    @Override
    public void dfs(V begin, VertexVisitor<V> visitor) {
        if (visitor == null) return;
        Vertex<V, E> vt = vertices.get(begin);
        if (vt == null) return;

//        dfs1(vt, new HashSet<>());
        dfs2(vt, visitor);
    }

    private void dfs1(Vertex<V, E> beginVt, Set<Vertex<V, E>> visitedVertices) {
        // 递归版
        System.out.println(beginVt.value);
        visitedVertices.add(beginVt);

        for (Edge<V, E> e : beginVt.outEdges) {
            if (visitedVertices.contains(e.to)) continue;
            dfs1(e.to, visitedVertices);
        }
    }

    public void dfs2(Vertex<V, E> beginVt, VertexVisitor<V> visitor) {
        // 非递归版

        Set<Vertex<V, E>> visitedVertices = new HashSet<>();
        Stack<Vertex<V, E>> stack = new Stack<>();

        stack.push(beginVt);                        // 1.先入栈起点
        if (visitor.visit(beginVt.value)) return;   // 2.访问
        visitedVertices.add(beginVt);               // 3.记录该点

        while (!stack.isEmpty()) {
            // 4.边的顶点出栈
            Vertex<V, E> vt = stack.pop();

            // 5.找到vt其中一条边，边的起、终点入栈（边的起点即为vt）
            for (Edge<V, E> e : vt.outEdges) {
                if (visitedVertices.contains(e.to)) continue;

                stack.push(vt);     // 边的起点 stack.push(e.from);
                stack.push(e.to);   // 边的终点
                if (visitor.visit(e.to.value)) return;
                visitedVertices.add(e.to);
                break;
            }
        }
    }

    @Override
    public List<V> topologicalSort() {
        List<V> list = new ArrayList<>();

        // topo排序只能针对有向无环图

        // 这里使用卡恩算法，但是卡恩算法思路是不断将图中入度为0的节点删掉
        // 这样会破坏图的结构，因此这里重新维护一个map，用来记录每个节点的入度
        // 根据操作不断更新map，已达到相同的的效果，且不会破坏原图结构
        Queue<Vertex<V, E>> queue = new LinkedList<>();
        Map<Vertex<V, E>, Integer> inDegreeMap = new HashMap<>();

        // 初始化（将入度为0的节点全部放入队列）
        // 为入度非0的节点创建入度表inDegreeMap
        vertices.forEach((V v, Vertex<V, E> vt)->{
            int vtInDegree = vt.inEdges.size();
            if (vtInDegree == 0) {
                queue.offer(vt);
            } else {
                inDegreeMap.put(vt, vtInDegree);
            }
        });

        while (!queue.isEmpty()) {
            Vertex<V, E> vt = queue.poll();

            // 结果放入list
            list.add(vt.value);

            for (Edge<V, E> e : vt.outEdges) {
                Vertex<V, E> to = e.to;
                int toInDegree = inDegreeMap.get(to) - 1;
                if (toInDegree == 0) {
                    queue.offer(to);
                } else {
                    inDegreeMap.put(to, toInDegree);
                }
            }
        }
        return list;
    }

    @Override
    public Set<EdgeInfo<V, E>> mst() {
        return prim(); // 时间复杂度可能更低
        // return kruskal();
    }

    private Set<EdgeInfo<V, E>> prim() {
        // 横切算法，被切中的边中选择权值最小的边
        // 1.在图G中任选一个点M1放入集合M，N为剩余所有点N的集合（G-M = N）
        // 2.切割M和N，将被切割边中最小权值边找出E1（连接M、N），将最小权值边的终点N1放入M，最小权值边E1放入A
        // 3.切割M和N，继续2，此步骤需要过滤掉之前已被选中A集合中的E1
        Iterator<Vertex<V, E>> iterator = vertices.values().iterator();
        if (!iterator.hasNext()) return null;

        Set<EdgeInfo<V, E>> results = new HashSet<>();
        Set<Vertex<V, E>> addedVertices = new HashSet<>();

        Vertex<V, E> vt = iterator.next();
        addedVertices.add(vt);
        MinHeap<Edge<V, E>> heap = new MinHeap<>(vt.outEdges, edgeComparator);
        // results.size() < vertices.size(): 最小生成树最多只会有vertices.size()-1条边
        // 因此如果results.size() == vertices.size()-1时，无需再往下切割
        int edgeSize = vertices.size() - 1;
        while (!heap.isEmpty() && results.size() < edgeSize) {
            Edge<V, E> e = heap.remove(); // 找到权值最小的那条边

            if (addedVertices.contains(e.to)) continue;
            results.add(e.info());
            addedVertices.add(e.to);
            heap.addAll(e.to.outEdges);
        }
        return results;
    }

    // O(E*logE)
    private Set<EdgeInfo<V, E>> kruskal() {
        // 按照边的权重（从小到大）加入到生成树中，直到生成树中含有n-1条边（n是顶点数）
        // 若改边加入会形成环，则不加入（从第三条边开始可能会形成环）
        int edgeSize = vertices.size() - 1;
        if (edgeSize == -1) return null;

        Set<EdgeInfo<V, E>> results = new HashSet<>();

        // O(logE)
        MinHeap<Edge<V, E>> heap = new MinHeap<>(edges, edgeComparator);

        // 每个顶点单独成一个集合
        // O(V)
        GenericUnionFind<Vertex<V, E>> uf = new GenericUnionFind<>();
        vertices.forEach((V v, Vertex<V, E> vt)->{
            uf.makeSet(vt);
        });

        // O(E*logE)
        while (!heap.isEmpty() && results.size() < edgeSize) {
            Edge<V, E> e = heap.remove(); // O(logE)

            // 边两头的顶点已经处于同一集合中就不能要，否则会造成环
            if (uf.isSame(e.from, e.to)) continue;

            results.add(e.info());
            uf.union(e.from, e.to);
        }
        return results;
    }

    @Override
    public Map<V, PathInfo<V, E>> shortestPath(V begin) {
//         return dijkstra(begin); // 不能有负权边(优化后效率更高)
        return bellmanFord(begin); // 可以有负权边（能检查出负权环）
    }

    // 贝尔曼-福特 算法 O(EV),E是边数，V是顶点数
    // 该算法不仅支持负权边，还可以检查出图中是否有负权环
    // 原理：对所有边进行V-1次松弛操作（V是节点数），得到所有可能的最短路径
    // 最好情况：第一轮就确定了所有点的最短路径，这样只需要1轮即可完成
    // 最坏情况：每一轮只能确定一个点的最短路劲，这样需要达到V-1轮才能完成
    private Map<V, PathInfo<V, E>> bellmanFord(V begin) {
        Vertex<V, E> beginVt = vertices.get(begin);
        if (beginVt == null) return null;

        // 记录起点到每个点的最短路径
        Map<V, PathInfo<V, E>>selectedPaths = new HashMap<>();
        // 初始状态，假设起点到起点的最短路径为0
        selectedPaths.put(beginVt.value, new PathInfo<>(weightManager.zero()));

        int count = vertices.size() - 1; // 松弛次数
        for (int i = 0; i < count; i++) {
            // 并不是一定需要V-1轮才能完成，有可能会提前结束
            // 如果某次所有边都未发送松弛操作，则说明松弛操作提前结束
            boolean isRelaxFinished = true;
            // 对每一条边都松弛V-1次
            for (Edge<V, E> edge : edges) {
                // 要松弛这条边先检查它起点的最短路径是否已经算出
                // 如果edge.from的最短路径没有，此条边松弛失败
                PathInfo<V, E> fromPathInfo = selectedPaths.get(edge.from.value);
                if (fromPathInfo == null) continue;
                if (relaxForBellmanFord(edge, fromPathInfo, selectedPaths)) {
                    isRelaxFinished = false;
                }
            }
            if (isRelaxFinished) {
                break;
            }
        }

        // 检查是否存在负权环
        // 思路：负权环的存在会导致每次松弛都会让最短路径发生改变，因此如果有负权环存在
        //      在经过V-1一轮循环后仍然会发生松弛操作
        // 这里再来一次，如果仍然存在松弛操作，则说明有负权环存在
        for (Edge<V, E> edge : edges) {
            PathInfo<V, E> fromPathInfo = selectedPaths.get(edge.from.value);
            if (fromPathInfo == null) continue;
            if (relaxForBellmanFord(edge, fromPathInfo, selectedPaths)) {
                System.out.println("求最短路径失败，存在负权环！");
                return null;
            }
        }

        selectedPaths.remove(begin);
        return selectedPaths;
    }

    private boolean relaxForBellmanFord(Edge<V, E> edge,
                                  PathInfo<V, E> fromPath,
                                  Map<V, PathInfo<V, E>> paths) {
        // 新的可以选择的最短路劲：beginVertex到edge.from的最短路径 + edge.weight
        E newWeight = weightManager.add(fromPath.weight, edge.weight);
        // 以前的最短路径：beginVertex到edge.to的最短路径
        PathInfo<V, E> oldPathInfo = paths.get(edge.to.value);

        // 如果以前的最短路径存在且更小，无需松弛
        if (oldPathInfo != null && weightManager.compare(oldPathInfo.weight, newWeight) <= 0) return false;

        if (oldPathInfo == null) {
            oldPathInfo = new PathInfo<>();
            paths.put(edge.to.value, oldPathInfo);
        } else {
            // 清掉以前的路径信息
            oldPathInfo.edgeInfos.clear();
        }

        oldPathInfo.weight = newWeight;
        oldPathInfo.edgeInfos.addAll(fromPath.edgeInfos);
        oldPathInfo.edgeInfos.add(edge.info());
        return true;
    }

    // 注意：迪杰斯特拉 算法不能有负权边
    // 迪杰斯特拉 算法可以优化至 O(E * LogV)，使用堆来求最短路径，可减少时间复杂度
    // 该算法可以理解为绳子相连的石头网，从桌面提前一个石头，其它石头被依次拉起的过程
    private Map<V, PathInfo<V, E>> dijkstra(V begin) {
        Vertex<V, E> beginVt = vertices.get(begin);
        if (beginVt == null) return null;

        Map<V, PathInfo<V, E>>selectedPaths = new HashMap<>();     // 记录结果表(离开桌面石头)
        Map<Vertex<V, E>, PathInfo<V, E>> paths = new HashMap<>(); // 下一次有可能被拉起的石头的最短路劲表（桌面上的石头）
        paths.put(beginVt, new PathInfo<>(weightManager.zero()));
        while (!paths.isEmpty()) {
            // 找到paths表中的最短路劲点
            Entry<Vertex<V, E>, PathInfo<V, E>> minEntry = getMinPath(paths);

            // minEntry 离开桌面
            Vertex<V, E> minVt = minEntry.getKey();
            PathInfo<V, E> minPath = minEntry.getValue();
            selectedPaths.put(minVt.value, minPath);
            paths.remove(minVt);

            // 对minVt的outEdges进行松弛操作
            for (Edge<V, E> e : minVt.outEdges) {
                // 如果e.to已经离开桌面就没必要进行松弛炒作
                if (selectedPaths.containsKey(e.to.value)) continue;
                relaxForDijkstra(e, minPath, paths);
            }
        }
        selectedPaths.remove(begin);
        return selectedPaths;
    }

    /**
     * 松弛操作
     * @param edge 需要进行松弛的边
     * @param fromPath edge边起点from目前的最短路径信息
     * @param paths 存放着其他点（对于dijkstra来说，就是还没有离开桌面的点）的最短路径信息
     */
    private void relaxForDijkstra(Edge<V, E> edge,
                                  PathInfo<V, E> fromPath,
                                  Map<Vertex<V, E>, PathInfo<V, E>> paths) {
        // 新的可以选择的最短路劲：beginVertex到edge.from的最短路径 + edge.weight
        E newWeight = weightManager.add(fromPath.weight, edge.weight);
        // 以前的最短路径：beginVertex到edge.to的最短路径
        PathInfo<V, E> oldPathInfo = paths.get(edge.to);

        // 如果以前的最短路径存在且更小
        if (oldPathInfo != null && weightManager.compare(oldPathInfo.weight, newWeight) <= 0) return;

        if (oldPathInfo == null) {
            oldPathInfo = new PathInfo<>();
            paths.put(edge.to, oldPathInfo);
        } else {
            // 清掉以前的路径信息
            oldPathInfo.edgeInfos.clear();
        }

        oldPathInfo.weight = newWeight;
        oldPathInfo.edgeInfos.addAll(fromPath.edgeInfos);
        oldPathInfo.edgeInfos.add(edge.info());
    }

    // 这里可以使用MinHeap堆优化，以优化至O(E * logV)级别，目前实现方式并未达到
    private Entry<Vertex<V, E>, PathInfo<V, E>> getMinPath(Map<Vertex<V, E>, PathInfo<V, E>> paths) {
        Iterator<Entry<Vertex<V, E>, PathInfo<V, E>>> it = paths.entrySet().iterator();
        Entry<Vertex<V, E>, PathInfo<V, E>> minEntry = it.next();
        while (it.hasNext()) {
            Entry<Vertex<V, E>, PathInfo<V, E>> entry = it.next();
            if (weightManager.compare(entry.getValue().weight, minEntry.getValue().weight) < 0) {
                minEntry = entry;
            }
        }
        return minEntry;
    }
}
