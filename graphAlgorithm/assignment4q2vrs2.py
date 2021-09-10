class Graph:
    def __init__(self, n):
        # array
        self.vertices = [None] * n
        for i in range(n):
            self.vertices[i] = Vertex(i)

    def add_edges(self, argv_edges):
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
    def __init__(self, vertex_id):
        self.id = vertex_id
        self.edges = []
        self.discovered = False
        self.predecessor = None
        self.distance = 0
        self.visited = False
        self.optimal_update = False

    def add_edge(self, edge):
        self.edges.append(edge)

    def added_to_queue(self):
        self.discovered = True


class Edge:
    def __init__(self, u, v, w):
        self.u = u
        self.v = v
        self.w = w


class Min_heap:
    def __init__(self):
        self.array = [(None, None)]

    def rise(self, k):
        while k > 1 and self.array[k][1] < self.array[k // 2][1]:
            self.swap(k, k // 2)
            k = k // 2

    def swap(self, vertex_one, vertex_two):
        self.array[vertex_one], self.array[vertex_two] = self.array[vertex_two], self.array[vertex_one]

    def sink(self, k):
        # while k has at least one child
        while 2 * k <= len(self.array)-1:
            child = self.smallest_child(k)
            if self.array[k][1] <= self.array[child][1]:
                break
            self.swap(child, k)
            k = child

    def smallest_child(self, k):
        if 2*k+1 > len(self.array)-1:
            return 2 * k
        elif self.array[2 * k][1] < self.array[2 * k + 1][1]:
            return 2 * k
        else:
            return 2 * k + 1

    def serve(self):
        self.swap(1, len(self.array)-1)
        vertex_tuple = self.array[len(self.array) - 1]
        self.array.pop()
        self.sink(1)
        return vertex_tuple

    def update(self, v, distance):
        for x in range(len(self.array)):
            if self.array[x][0] == v:
                self.array[x] = (v, distance)
                self.sink(x)
                self.rise(x)
                break


def opt_delivery(n, roads, start, end, delivery):
    delivered = False
    route = [end]
    my_graph = Graph(n)
    my_graph.add_edges(roads)
    discovered = Min_heap()
    discovered.array.append((start, 0))
    my_graph.vertices[start].distance = 0
    my_graph.vertices[start].discovered = True
    my_graph.vertices[start].predecessor = start
    while len(discovered.array) > 1:
        serving_vertex = discovered.serve()  # A tuple (v, optimal distance from source)
        for edge in my_graph.vertices[serving_vertex[0]].edges:
            v = my_graph.vertices[edge.v]
            u = my_graph.vertices[edge.u]
            if not v.discovered and not v.optimal_update:
                v.discovered = True
                if u.id == delivery[0] and v.id == delivery[1] and not delivered:
                    delivered = True
                    v.distance = u.distance + edge.w - delivery[2]
                else:
                    v.distance = u.distance + edge.w
                v.predecessor = u.id
                discovered.array.append((edge.v, v.distance))
                discovered.rise(len(discovered.array)-1)
            else:
                if u.id == delivery[0] and v.id == delivery[1] and not delivered:
                    if v.distance > u.distance + edge.w - delivery[2]:
                        delivered = True
                        v.distance = u.distance + edge.w - delivery[2]
                        v.predecessor = u.id
                        discovered.update(v.id, v.distance)
                        u.discovered = False
                        u.optimal_update = True
                        discovered.array.append((edge.u, u.distance))
                        discovered.rise(len(discovered.array) - 1)
                else:
                    if v.distance > u.distance + edge.w:
                        v.distance = u.distance + edge.w
                        v.predecessor = u.id
                        discovered.update(v.id, v.distance)
                        u.discovered = False
                        u.optimal_update = True
                        discovered.array.append((edge.u, u.distance))
                        discovered.rise(len(discovered.array) - 1)

    for v in my_graph.vertices:
        print(start," to ",v.id," having distance of ",v.distance," from ",v.predecessor)

    optimal_distance = my_graph.vertices[end].distance
    current = my_graph.vertices[end]
    # while current.predecessor is not None:
    #     route.append(current.predecessor)
    #     current = my_graph.vertices[current.predecessor]
    # route.reverse()
    print(optimal_distance, route)


if __name__ == '__main__':
    n = 4
    roads = [(0, 1, 3), (0, 2, 5), (2, 3, 7), (1, 3, 20)]
    start = 0
    end = 1
    delivery = (2, 3, 25)
    profit = 25
    opt_delivery(n, roads, start, end, delivery)
