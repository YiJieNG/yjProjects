"""31158145 YI JIE NG FIT2004 Assignment 4"""


def best_trades(prices, starting_liquid, max_trades, townspeople):
    """
    # Task 1
    best_trades function consider array list of prices which represent the price of each liquid according to their
    respective index, integer of starting_liquid to act as source, integer of max_trades which determines the trades
    limit that we could carry out and list of lists for townspeople which shows the possible trades provided from each
    person from the town.
    Since the townspeople already act as a list of edges, the function will just loop through the townspeople(after
    going through pre processing progress) under maximum trades provided hence Big O-Complexity will be O(T * M) where
    T is the total number of trades available(townspeople) and the M is the max_trades.
    best_trades function returns double of max_profit which represents the maximum profit we can get after finishing
    the trades with townspeople for max_trades round.
    """

    # pre processing
    # Since the townspeople is a list of lists, we can just make them as one list and it will become a list which stores
    # all the edges and represent as a available trade list.
    # Big-O Complexity: O(T) where T is the total number of trades available since O(x) + O(y) will be O(T)
    trade_list = []
    for x in range(len(townspeople)):
        for y in range(len(townspeople[x])):
            trade_list.append(townspeople[x][y])

    # initialise profit ratio list
    profit = [0] * len(prices)
    for z in range(len(profit)):
        if z == starting_liquid:
            profit[z] = 1

    # Under every single available trade
    for i in range(max_trades):
        # initialise a temp list which updates the profit list
        temp = len(profit) * [0]
        # search through the trade list
        for j in range(len(trade_list)):
            u = trade_list[j][0]
            v = trade_list[j][1]
            w = trade_list[j][2]
            # if current liquid profit ratio(since ratio thus use multiply instead of addition) * w(ratio provided from
            # trade list from townspeople) greater than the ratio recorded previously means it is profitable to trade
            # for these liquids, update the list(second condition is to select the more profitable trade if there is a
            # duplicate case occurs)
            if profit[u] * w > profit[v] and profit[u] * w > temp[v]:
                temp[v] = profit[u] * w
        # update the list
        for k in range(len(temp)):
            if temp[k] != 0:
                profit[k] = temp[k]

    # Since we got the biggest ratio for each liquid, multiply all the liquids with their corresponding ratio
    max_profit = -1
    for n in range(len(profit)):
        # find the most profitable liquid among the all liquids
        if (profit[n] * prices[n]) > max_profit:
            max_profit = profit[n] * prices[n]

    return max_profit


###########################################################################################
class Graph:
    """
    # Task 2
    Class Graph creates all the vertices and edges used in task 2. It creates and initialises the vertices array
    with n size of vertex from Vertex class.
    Big-O Complexity of constructor = O(n): Creation of vertices array which calls Vertex class n times and store them
    at the vertices array.
    """
    def __init__(self, n):
        # vertices array
        self.vertices = [None] * n
        # creates vertex object and store them into array
        for i in range(n):
            self.vertices[i] = Vertex(i)

    """
    Method add_edges take argv_edges array list as input. For each edges（u, v, w）store inside the argv_edges, store 
    them to their corresponding position in the vertex based on u. (u = starting vertex, v = ending vertex, w = weight)
    Big-O Complexity: O(N) where N is the total number of roads. Since it is an undirected graph, we need to store u to 
    v as well as v to u. T(N) will be T(2N) but since 2 is a constant we just ignore it during Big O complexity.
    """
    def add_edges(self, argv_edges):
        # for each edge from argv_edges
        for edges in argv_edges:
            u = edges[0]
            v = edges[1]
            w = edges[2]
            # add edge for current vertex
            current_edge = Edge(u, v, w)
            current_vertex = self.vertices[u]
            current_vertex.add_edge(current_edge)

            # since it is undirected graph, add same edge for v to u
            current_edge = Edge(v, u, w)
            current_vertex = self.vertices[v]
            current_vertex.add_edge(current_edge)


class Vertex:
    """
    Class Vertex creates and initialises all the payload used in task 2 including id of vertex, edges list of the vertex
    , discovered from min heap or not, its predecessor, cost and visited from min heap or not. It basically records all
    the details of a vertex taking an input of vertex_id.
    Big-O Complexity of constructor: O(1)
    """
    def __init__(self, vertex_id):
        self.id = vertex_id         # id of a vertex
        self.edges = []             # adjacent edges list
        self.discovered = False     # discovered status for a min heap
        self.predecessor = None     # predecessor of the vertex
        self.cost = float("inf")    # total cost from source
        self.heap_id = 0            # heap id of vertex in heap

    """
    Function add_edge add the edge for the vertex into its adjacent edges list
    Big-O Complexity: O(1)
    """
    def add_edge(self, edge):
        self.edges.append(edge)


class Edge:
    """
    Class Edge creates and initialises u(starting vertex), v(ending vertex) and w(weight/cost) in order to store the
    details of the edge.
    Big-O Complexity of constructor: O(1)
    """
    def __init__(self, u, v, w):
        self.u = u
        self.v = v
        self.w = w


class Min_heap:
    """
    Class Min_heap which carries out a min heap queue for graph in order to determine the serving priority of each
    discovered edge. It creates and initialises the array with (None) since heap needs to start an array from
    index of 1 instead of 0.
    Big-O Complexity of constructor: O(1)
    """
    def __init__(self):
        self.array = [None]

    """
    Method rise ensure the edge was rise to its corresponding position by checking its parent edge weight value. It  
    takes input of k which represents the current position of the edge that we wants to rise.
    Big-O Complexity: O(log N) where N is the heap array list length.
    """
    def rise(self, k):
        # while it is not the root and its parent weight is larger
        while k > 1 and self.array[k].cost < self.array[k // 2].cost:
            self.swap(k, k // 2)
            self.array[k].heap_id = k
            self.array[k // 2].heap_id = k//2
            k = k // 2

    """
    Method swap swaps the position of two inputs index given from the heap array list.
    Big-O Complexity: O(1)
    """
    def swap(self, one, two):
        self.array[one], self.array[two] = self.array[two], self.array[one]

    """
    Method sink ensure the edge was sink to its corresponding position by checking its children weight value. It takes
    input of k which represents the current position of the edge that we wants to sink.
    Big-O Complexity: O(log N) where N is the heap array list length.
    """
    def sink(self, k):
        # while k has at least one child
        while 2 * k <= len(self.array)-1:
            child = self.smallest_child(k)
            if self.array[k].cost <= self.array[child].cost:
                break
            self.swap(child, k)
            self.array[k].heap_id = k
            self.array[child].heap_id = child
            k = child

    """
    Method smallest_child determines the smallest child of current edge(based on input of k) and return its position.
    Big-O Complexity: O(1)
    """
    def smallest_child(self, k):
        # if there is only one child, return the one
        if 2*k+1 > len(self.array)-1:
            return 2 * k
        # else compare two children and return the smallest one
        elif self.array[2 * k].cost < self.array[2 * k + 1].cost:
            return 2 * k
        else:
            return 2 * k + 1

    """
    Method serve swap the current smallest edge with the last edge from the array list of the min heap, pop the 
    current last edge(current minimum edge of the min heap array list), and then sink the edge we swap to first position
    previously.
    Big-O Complexity: O(log N) where N is the heap array list length.
    """
    def serve(self):
        self.swap(1, len(self.array)-1)                 # O(1) for swap
        self.array[1].heap_id = 1
        self.array[len(self.array)-1].heap_id = 0
        vertex = self.array.pop()                       # O(1) for pop
        self.sink(1)                                    # O(log N) for sink
        return vertex


def dijkstra(n, roads, start, end):
    """
    Task 2. Function dijkstra takes int n as number of cities, roads(list of tuples) as the possible route, start as
    starting city and end as destination. It stores the cities as the vertices and roads as the edges inside the graph
    class. Min_heap is then called to act as a heap queue to apply the greedy algorithm which choose the shortest path.
    After finished running the Min_heap, we can know all teh shortest distance from starting city to every other city.
    We then record the path from end to start which consider the predecessor from every vertex as reference. We reverse
    it since it is from end to start currently. Finally we return the optimal distance and the path from start to end.
    Big-O Complexity: O(R log N) where R is the total number of roads and N is the total number of cities. R is for the
    edges for each city whereas N is for the Min_heap process.
    """
    route = [end]       # a list to store the optimal path from end to start
    my_graph = Graph(n)     # assign a graph class to my_graph (O(n))
    my_graph.add_edges(roads)      # add all the roads to their corresponding position at adjacent list from vertex.
    discovered = Min_heap()     # assign a Min_heap class to discovered (O(1))
    discovered.array.append(my_graph.vertices[start])     # adding source to the Min_heap
    my_graph.vertices[start].cost = 0   # source to source will be cost for 0 initially
    my_graph.vertices[start].discovered = True  # update discovered status
    my_graph.vertices[start].heap_id = 1    # assign heap id

    # Start running through heap. Loop when there is at least one edge inside the heap wait to serve. O(log N)
    while len(discovered.array) > 1:
        serving_vertex = discovered.serve()
        # loop through the edges for current serving vertex. O(R)
        for edge in my_graph.vertices[serving_vertex.id].edges:
            v = my_graph.vertices[edge.v]
            u = my_graph.vertices[edge.u]
            # if the city has not been discovered yet, update the vertex details
            if not v.discovered:
                v.discovered = True
                v.cost = u.cost + edge.w
                v.predecessor = u.id        # records the predecessor
                # append the edge to the heap to wait for its turn to be serve
                discovered.array.append(v)
                v.heap_id = len(discovered.array) - 1
                # rise to its corresponding position in heap since it is a new added tuple
                discovered.rise(v.heap_id)
            # else update the details if current cost is not the smaller one
            else:
                if v.cost > u.cost + edge.w:
                    v.cost = u.cost + edge.w
                    v.predecessor = u.id
                    discovered.rise(v.heap_id)

    # records the optimal cost
    optimal_cost = my_graph.vertices[end].cost
    current = my_graph.vertices[end]
    # traceback the path from end to start
    # O(R) where R is the roads list size
    while current.predecessor is not None:
        if current.predecessor is not current.id:
            route.append(current.predecessor)
        current = my_graph.vertices[current.predecessor]

    # reverse the list from end to start to start to end
    # O(R) where R is the size of route list
    route.reverse()
    return optimal_cost, route


def opt_delivery(n, roads, start, end, delivery):
    """
    Task 2. Function opt_delivery finds the optimal route from starting city(start) to destination(end) by comparing
    two optimal distance which is: 1) normal route from starting city to destination. 2) start to pick up city + detour
    + drop item city to end. Return the smaller(optimal) one.
    Big-O Complexity: O(R log N) where R is the total number of roads and N is the total number of cities. Since
    dijkstra function will be called three times (normal, start to pick up, pick up to drop down, drop down to end), run
    time complexity will be T(4R log N) where 4 is a constant thus it can be ignored.
    """
    # call dijkstra for normal route. O(R log N)
    normal_optimal, normal_route = dijkstra(n, roads, start, end)

    # call dijkstra for route that need to consider delivery as detour. O(R log N)
    source_pick_up_optimal, source_pick_up_route = dijkstra(n, roads, start, delivery[0])
    drop_end_optimal, drop_end_route = dijkstra(n, roads, delivery[1], end)
    temp_optimal, temp_route = dijkstra(n, roads, delivery[0], delivery[1])

    # add the route of start to pick up and drop to end with delivery route
    pick_up_optimal = source_pick_up_optimal + drop_end_optimal + temp_optimal - delivery[2]
    # return optimal route
    if normal_optimal <= pick_up_optimal:
        ans = (normal_optimal, normal_route)
    else:
        # merge all the three routes together. O(R), T(2R)
        final_route = source_pick_up_route
        for y in temp_route:
            if final_route[-1] != y:
                final_route.append(y)
        for z in drop_end_route:
            if final_route[-1] != z:
                final_route.append(z)
        ans = (pick_up_optimal, final_route)
    return ans
