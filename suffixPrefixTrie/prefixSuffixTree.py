"""31158145 YI JIE NG FIT2004 Assignment 3"""


class q1_Node:
    """
    # Task 1
    Class q1_Node creates the node for the prefix trie used in task 1. It creates and initialises all the payload needed
    when the node is created.
    Big-O Complexity of constructor = O(1): All creation of payloads takes O(1) except self.link having O(5). Since
    O(5) is a constant complexity, its Big-O complexity will be considered as O(1) as well.
    """
    def __init__(self):
        # link for each and every node
        self.link = [None] * 5
        # frequency for the particular dna
        self.frequency = 0
        # dna of node in its index form (terminal=0, A=1, B=2, C=3, D=4)
        self.dna = None
        # record current best optimal frequency node 
        self.optimal = None
        # record index of next character in order to compare the lexicographical order
        self.nextChar = None
        # the dna Sequence
        self.dnaSequence = None


class SequenceDatabase:
    """
    # Task 1
    Class SequenceDatabase creates a prefix trie to let user search the best optimal frequency of dnaSequence base on
    the dna query they entered. The constructor create a node as the root of the trie.
    Big-O Complexity of constructor = O(1)
    """
    def __init__(self):
        self.root = q1_Node()

    """
    Function addSequence takes the dnaSequence as input, call the root and store it as current node, pass the current 
    node and dnaSequence to its auxiliary function. When the frequency and the optimal node for each character node in 
    dnaSequence are updated, the root node itself has to update its frequency and optimal node as well in order to 
    handle the empty string input at query function.
    Big-O Complexity = O(Complexity of its auxiliary function) --> O(n) where n is the length of dna input.
    """
    def addSequence(self, dna):
        # assign dna input and current node and pass them to auxiliary function of addSequence
        # Complexity = O(Complexity of auxiliary function)
        current = self.root
        # return the child node
        child = self.addSequence_aux(current, dna)

        # update frequency and optimal node if necessary
        # update condition:
        # 1. if the current optimal frequency is smaller than child frequency, update the current optimal node
        if current.frequency < child.frequency:
            current.frequency = child.frequency
            current.optimal = child.optimal
            current.nextChar = child.dna

        # 2. if frequency is the same,
        elif current.frequency == child.frequency:
            # if dna index of child node is different with current optimal dna index, choose the lexicographical
            # smaller character as optimal node(compare with dna index stored. (terminal=0, A=1, B=2, C=3, D=4). If both
            # dna index are the same, keep updating them to ensure the best optimal node is passed all the way to root.
            if child.dna <= current.nextChar:
                current.optimal = child.optimal
                current.nextChar = child.dna

    """
    Function addSequence_aux takes the current node and dna passed by addSequence, creates and initialise the new 
    char_count as 0 (if char_count input is not passed. If initialise, takes the input) as the input. Starting from root
    node, it will enter the next level(next depth) of node base on the link of current node stored by using index of 
    ord(dna[char_count]). If the link exist, it will enter the next layer or else create a new node. Everytime it 
    created the new node link, it will assign the new node.dna an index which represent the index of character at its 
    respective level. When it reached the terminal node(terminal), it will increment the terminal node frequency and 
    assign the dna index as 0 (terminal index), make optimal node of terminal node itself and assign its respective 
    dnaSequence. When it goes back to its parent node (since it is recursion), the parent node will assign the optimal 
    node to its current.optimal and return the current node to its parent node. Such process will repeat until it 
    returned back to the top level(root node) of the trie.
    Big-O Complexity: O(n) where n is the length of dna input. Since it will do the recursion from top to terminal, and 
    return back to top level again, complexity will be O(2n).
    """
    def addSequence_aux(self, current, dna, char_count=0):
        # base case: reach terminal node
        if char_count == len(dna):
            # if new dnaSequence is assigned, create a new terminal node
            if current.link[0] is None:
                current.link[0] = q1_Node()
            # else enter the terminal node
            current = current.link[0]
            # update the frequency, dna index, dna sequence and optimal node
            current.frequency += 1
            current.dna = 0
            current.dnaSequence = dna
            current.optimal = current
            # return current node
            return current

        # recursion: enter the next node
        else:
            # determine the index base on character of dna
            index = ord(dna[char_count])-64
            # if next char node is not exist, create a new one
            if current.link[index] is None:
                current.link[index] = q1_Node()
            # enter next node
            current = current.link[index]
            # assign current node index base on its character (next node)
            current.dna = index
            # increment character counter
            char_count += 1
            # enter the next recursive level
            # when it is exiting from lower level, assign the return child node to child
            child = self.addSequence_aux(current, dna, char_count)

            # update frequency and optimal node if necessary
            # update condition:
            # 1. if the current optimal frequency is smaller than child frequency, update the current optimal node
            if current.frequency < child.frequency:
                current.frequency = child.frequency
                current.optimal = child.optimal
                current.nextChar = child.dna

            # 2. if frequency is the same,
            elif current.frequency == child.frequency:
                # if dna index of child node is different with current optimal dna index, choose the lexicographical
                # smaller character as optimal node(compare with dna index stored. (terminal=0, A=1, B=2, C=3, D=4).
                # If both dna index are the same, keep updating them in order to ensure the best optimal node is passed
                # all the way to the root node.
                if child.dna <= current.nextChar:
                    current.optimal = child.optimal
                    current.nextChar = child.dna

            # return the current node
            return current

    """
    Function query takes the dna as input, call the root and store it as current node, pass the current node and dna to 
    its auxiliary function. It return the dnaSequence which return from its auxiliary function.
    Big-O Complexity = O(Complexity of its auxiliary function) --> O(q) where q is the length of dna input of query
    """
    def query(self, dna):
        current = self.root
        return self.query_aux(current, dna)

    """
    Function query_aux takes the current node, dna as input, creates and initialise the new char_count as 0 (if 
    char_count input is not passed. If initialise, takes the input) as input. If the current node is match with dna 
    length or if dna is an empty string, it has reached the deepest level node and return the dnaSequence stored by 
    optimal code at current node. 
    Big-O Complexity = O(q) where q is the length of dna input of query since it needs enter q depth/level of node to 
    get the optimal and return back to top level node and it takes O(2q) to run through.
    """
    def query_aux(self, current, dna, char_count=0):
        # base case: char_count is equal to dna length or it is an empty string
        if char_count == len(dna) or dna == "":
            # if the optimal node of the dna enter is empty, return None else return dnaSequence stored by optimal node
            if current.optimal is None:
                return None
            else:
                return current.optimal.dnaSequence
        # recursion: enter next node
        else:
            # determine the index base on character of dna
            index = ord(dna[char_count])-64
            # if no char node exist means no dnaSequence of such query in the trie, return None
            if current.link[index] is None:
                return None
            # else enter next level node
            else:
                current = current.link[index]
                char_count += 1
                return self.query_aux(current, dna, char_count)


###########################################################################################
class q2_Node:
    """
    # Task 2
    Class q2_Node creates the node for the suffix trie used in task 2. It creates and initialises all the payload needed
    when the node is created.
    Big-O Complexity of constructor = O(1): Creation of dna_id takes O(1) while self.link takes O(5). Since O(5) is a
    constant complexity, its Big-O complexity will be considered as O(1) as well.
    """
    def __init__(self):
        # terminal at index 0
        self.link = [None] * 5
        # a list which record the current node possible suffix dna_id based on the prefix given
        self.dna_id = []


class OrfFinder:
    """
    # Task 2
    Class OrfFinder creates two suffix tries to store the suffix id for each and every suffix words generated from the
    dna input pass by user from the constructor. It then provides the find service for user to input the prefix and
    suffix of the word they wish to form for a substring form genome. The constructor creates a node as the root of the
    trie for start suffix trie and end suffix trie respectively. It then creates the both trie based on the genome input
    Big-O Complexity of constructor = O(N**2) where N is the length of genome/dna word base on the input "dna". For each
    trie, it creates the node and terminal for their suffix words(N types of words) with N depth thus O(N**2). Since
    there are two tries created, complexity = O(2(N**2)).
    """
    def __init__(self, dna):
        # record the genome/dna word input
        self.dna = dna
        # initialise a start root node for start trie
        self.start_root = q2_Node()
        # initialise an end root node for end trie
        self.end_root = q2_Node()
        # build a start suffix trie
        self.start_suffix(dna)
        # build an end suffix trie
        self.end_suffix(dna)

    """
    Function start_suffix takes dna as the input which stores the genome or words of dna. It initialises the current 
    node as its respective root node and pass the current node, dna_id(suffix index), dna(genome), char_count(counter)
    and status of start since we are creating suffix trie starting from front. 
    Big-O Complexity = O(N) * O(Complexity of its auxiliary function) --> O(N**2) where N is the length of the genome.
    """
    def start_suffix(self, dna):
        # initialise the current node as root node
        current = self.start_root
        # since it is a start trie, dna_id will start from 0 until N and assign all dna_id to its all suffix word nodes
        for dna_id in range(len(dna)):
            char_count = dna_id
            self.suffix_aux(current, dna_id, dna, char_count, status="start")

    """
    Function end_suffix takes dna as the input which stores the genome or words of dna. It initialises the current 
    node as its respective root node and pass the current node, dna_id(suffix index), dna(genome), char_count(counter)
    and status of end since we are creating suffix trie starting from back. 
    Big-O Complexity = O(N) * O(Complexity of its auxiliary function) --> O(N**2) where N is the length of the genome.
    """
    def end_suffix(self, dna):
        # initialise the current node as root node
        current = self.end_root
        # since it is an end trie, dna_id will start from N until 0 and assign all dna_id to its all suffix word nodes
        for dna_id in range(len(dna)-1, -1, -1):
            char_count = dna_id
            self.suffix_aux(current, dna_id, dna, char_count, status="end")

    """
    Function suffix_aux takes the input from its parent function(either start_suffix or end_suffix), assigns all the 
    suffix words to their own respective terminal nodes, records their current node possible suffix id when it moves to
    next level node. If the status is showing "start" means currently creating start suffix trie, char_count increment
    during every nodes level or else decrement since it is an end suffix trie.
    Big-O Complexity = O(N) where N is the length of the genome since it needs to create N level nodes.
    """
    def suffix_aux(self, current, dna_id, dna, char_count, status):
        # base case
        # base on the status given, start trie base case will be char_count = last word, end trie will be = end word
        if (char_count == len(dna) and status == "start") or (char_count == -1 and status == "end"):
            # if terminal is first time created, create new node and append the suffix id at the particular terminal
            if current.link[0] is None:
                current.link[0] = q2_Node()
            current = current.link[0]
            current.dna_id.append(dna_id)
            return 
        # recursion
        # if current node is not the terminal node/ have not reach the len(dna)
        else:
            # determine the index of current character
            index = ord(dna[char_count])-64
            # if there is no path exist, create a new node for it
            if current.link[index] is None:
                current.link[index] = q2_Node()
            # assign the next level node as current node(enter next level node)
            current = current.link[index]
            # record its respective suffix index in order to record the possible suffix id with current prefix word
            current.dna_id.append(dna_id)
            # update the char_count base on the status given
            if status == "start":
                char_count += 1
            elif status == "end":
                char_count -= 1

            # enter next node
            return self.suffix_aux(current, dna_id, dna, char_count, status)

    """
    Function find_start search through the start suffix trie created before with level of length(dna_start) input in
    order to get the all the possible dna_id starting with prefix dna_start.
    Big-O Complexity = O(Complexity of auxiliary function) --> O(D) where D is the length of dna_start
    """
    def find_start(self, dna_start):
        # call the start suffix trie we created before
        current = self.start_root
        # enter the auxiliary function
        return self.find_aux(current, dna_start, char_count=0, status="start")

    """
    Function find_start search through the end suffix trie created before with level of length(dna_end) input in
    order to get the all the possible dna_id ending with suffix dna_end.
    Big-O Complexity = O(Complexity of auxiliary function) --> O(D) where D is the length of dna_end
    """
    def find_end(self, dna_end):
        # call the end suffix trie we created before
        current = self.end_root
        # enter the auxiliary function
        return self.find_aux(current, dna_end, char_count= len(dna_end)-1, status="end")

    """
    Function find_aux takes input of current node, dna(prefix/suffix we wish to search base on the status) and status,
    searching through the trie based on len(dna) and return all the possible suffix dna_id.
    Big-O Complexity = O(D) where D is the length of dna input from the parent function(either find_start or find_end)
    since it need to pass through D level of node. 
    """
    def find_aux(self, current, dna, char_count, status):
        # base case
        # base on the status given, start trie base case will be char_count = last word, end trie will be = end word
        if (char_count == len(dna) and status == "start") or (char_count == -1 and status == "end"):
            # return the possible suffix id base on current prefix node
            return current.dna_id
        # recursion
        # if current node is not the terminal node/ have not reach the len(dna)
        else:
            # determine the index base on the char_count
            index = ord(dna[char_count])-64
            # if there is no path exist means no such possible suffix word with the prefix word given, return None
            if current.link[index] is None:
                return None
            # else enter the next level of node, update char_count base on the status input
            else:
                current = current.link[index]
                if status == "start":
                    char_count += 1
                elif status == "end":
                    char_count -= 1
                return self.find_aux(current, dna, char_count, status)

    """
    Function find takes input of dna_start and dna_end as the prefix and suffix of user wish to start and end in order
    to form a substring based on the genome. It will return the all possible formation of the substring after searching 
    through start_trie and end_trie, validate the possibilities for each id return.
    Big-O Complexity = O(len(dna_start) + len(dna_end) + U) where U is the number of characters in the output list since
    it search through the starting trie and ending trie, return the output using slicing(O(N)) based on the valid id 
    list generated(N times) thus U = O(N**2) if there is any output exist. If there is no output exist from the tries we 
    searched, return None before validating the id list and thus U = O(1).
    """
    def find(self, dna_start, dna_end):
        # an array to store all the correct substring
        ans = []
        # assign the genome
        dna = self.dna
        # an array which stores the valid id
        id_valid = []
        # assign all the possible start suffix id to start_id array
        start_id = self.find_start(dna_start)
        # assign all the possible end suffix id to end_array
        end_id = self.find_end(dna_end)
        # if either end_id or start_id has no possibilities to generate the substring, return [] to achieve O(1)
        if end_id is None or start_id is None:
            return ans
        # validating all the possible starting id and ending id return
        # Complexity = O(N**2)
        for start in start_id:
            for end in end_id:
                # if the id is valid, append them to id_valid array with (start_id, end_id+1)
                # Valid condition:
                # 1. start id is smaller then end id
                # This is to prevent the non-substring to be return
                if start < end:
                    # 2. if subtraction of end+1 and start is smaller or equal to len(dna_start) + len(dna_end)
                    # This is to avoid any overlapping substring to be return
                    if end + 1 - start >= len(dna_start) + len(dna_end):
                        id_valid.append((start, end + 1))

        # append all the valid substring into ans array and return the ans which stored the valid substring
        # Complexity = O(N**2)
        for x in range(len(id_valid)):
            ans.append(dna[id_valid[x][0]: id_valid[x][1]])
        return ans
