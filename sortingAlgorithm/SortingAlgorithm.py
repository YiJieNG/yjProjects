"""31158145 YI JIE NG FIT2004 Assignment 1"""

# #########################################################################################################################################
"""
# Task 1
Function best_interval takes input of transaction which having a list of integer record and t as the best_iterval length.
The function should return a tuple which first number represent starting time of the best interval transaction. The second number represent the total element count within the range of starting time and starting time + t.
The complexity of the function will be O(nk) where n is size of list and k is the greatest number of digits in any element in transactions.
"""


def best_interval(transaction, t):
    # if transaction is an empty list, the list is already sorted and its starting time and count will always be 0.
    if len(transaction) == 0:
        return 0, 0
    base = 10   

    # Radix sort is applied and its complexity will be O(nk). n is the size of the list while k is the largest digit number from the list.
    max_item = transaction[0]   
    for item in transaction:  
        if item > max_item:
            max_item = item
    length = len(str(max_item))
    
    for col in range(length):   # compare number digit by digit O(k)
        # counting sort applied O(n)
        max_item = (transaction[0]//(base**col))%base   # use this formula to find the column
        for item in transaction:
            if (item//(base**col))%base > max_item:
                max_item = (item//(base**col))%base
        count_array = [None] * (max_item+1)
        # avoid list reference error
        for i in range(len(count_array)):
            count_array[i] = []
        # update count array
        for item in transaction:
            count_array[(item//(base**col))%base].append(item)
        # update input array
        index = 0
        for x in range(len(count_array)):
            if count_array[x] != []:
                for y in range(len(count_array[x])):
                    transaction[index] = count_array[x][y]
                    index = index + 1


    # Create a list to store all the unique number and its index(earliset in the list if duplicate). O(n) where n is size of transaction
    j_next = []
    for x in range(len(transaction)):
        if x == len(transaction) - 1:
            j_next.append((transaction[x], x))
            break
        elif transaction[x] != transaction[x + 1]:
            j_next.append((transaction[x], x))
    index_j_next = len(j_next) - 1


    # Since the interval return has to be the smallest, we will run through the list in a reverse way(start from list[-1]).
    # Pointer i will be the one who check through each and every single element inside the list. Pointer j will be the one who act as the current starting + t.
    # Elements between i and j is within the range of current interval.
    # The complexity will be O(n + nt) which is smaller than O(nk). Where n is the size of the sorted list while t is the interval.
    # Pointer i moved through all the way of list. 
    # For each and every movement of pointer i, pointer j will only be updated when current j element is out of range of its start + t.
    # If pointer j does not need to be updated, there will be no complexity for O(x) but if it does, it search through the current interval by its unique numbers inside only with the help of j_next list we created.(O(t))
    i = len(transaction) - 1   
    j = len(transaction) - 1   
    count = 0   
    start = transaction[-1] 

    # while pointer i have not run through all the elements inside the list
    while i >= 0:   
        # if pointer i has come to the end of the list(base case)
        if i == 0:  
            # since there is no more element can be compared, count the length(represent number of elements)
            current_count = j - i + 1   
            if current_count >= count:     
                count = current_count
                # update start. Since smallest start need to be output, if element[j]-t is negative, start will always be 0. Else start = element[j]-t.
                if transaction[j] - t <= 0:
                    start = 0
                else:
                    start = transaction[j] - t
            break
        # if the next element of i is out of range of start and start + t means the next element will never be the start. update pointer j.
        if transaction[i - 1] < transaction[j] - t:
            current_count = j - i + 1   #finish current interval work
            if current_count >= count:
                count = current_count
                start = transaction[j] - t  
            for loop in range(index_j_next, -1, -1):    #loop through current interval and search its next start. 
                if j_next[loop][0] <= transaction[i - 1] + t:
                    j = j_next[loop][1]
                    index_j_next = loop
                    # break the iteration once we found the first start
                    break
                
        i -= 1  # compare next element
    return start, count


# #########################################################################################################################################
"""
# Task2
Function word_with_anagram take two string lists where list2 is the reference list for list1 to compare while list1 need to check which element inside is anagram with list2.
The function will return a list which contains all the anagram inisde list1.
The complexity will be O(L1M1 + L2M2) where L1 and L2 is the number of elements in list1 and list2 respectively while M1 and M2 is the number of characters in the longest string in list1 and list2 correspondingly
"""


def words_with_anagrams(list1, list2):
    # if one of the input list is empty or even both are empty, return []
    if list1 == [] or list2 == []:
        return []
    
    # in order to let list1 remember its original word, index is recorded for each and every elements inside list1. O(L1)
    for x in range(len(list1)):
        element_ID = (list1[x], x)
        list1[x] = element_ID
    # index of 0 for every element in list2 are recorded as well since we need to access the following sorting functions. O(L2)
    for y in range(len(list2)):
        element_ID = (list2[y], 0)
        list2[y] = element_ID
    
    # In order to sort a list and check the anagram, we first need to sort each and every character from its own word since anagram word will have the same number alphabet combination.
    # Then we sort the list according to each and every length of the word from the list in order to do the pre processing step.
    # Finally we can use the radix sort(pre processing is applied since we have longest length) to sort the list.

    list2 = sort_counting_charinWord(list2)
    list2, length = sortbylength(list2)
    list2 = sort_radix_string(list2, length)
    # remove index((word, index)) for list2 since it is redundant for a reference list
    for z in range(len(list2)):
        element = list2[z][0]
        list2[z] = element

    # remove duplicate in place for list2 which act as a reference list. O(L2) where L2 is the size of list2
    # ***refer to Monash University FIT2004 tutorial week 3 Question 6***
    i = 0
    j = 0
    while i < len(list2)-1 and j < len(list2):
        word1 = list2[j]
        word2 = list2[i+1]
        if len(word1) == len(word2):
            same = True
            for x in range(len(word1)):
                if word1[x] != word2[x]:
                    same = False
                    break
            if same:
                i += 1
        if i == len(list2) - 1:
            break
        list2[j+1], list2[i+1] = list2[i+1], list2[j+1]
        j += 1
        i += 1
    list2 = list2[0:j+1]    

    # Sort list1 as what we did for list2 above. The only difference is in list2 we update the list2 during the sorting progress.
    # For list1, since we need to know what the original word for the anagram is, we have to remain the original list and store the sorted list1 into a new list.
    list1_sort = sort_counting_charinWord(list1)
    list1_sort, length = sortbylength(list1_sort)
    list1_sort = sort_radix_string(list1_sort, length)


    # To compare both list in order to find the anagram words, we give list1 a pointer(pointer1) and list2(pointer2) as well.
    # Since we are comparing words by words for both lists in place(since they are sorted), the complexity will be O(L1M1 + L2M2) as mentioned at the beginning of the function(Line 94).
    pointer1 = 0
    pointer2 = 0
    anagram_list = []   # list to record the anagram words
    while pointer1 <= len(list1_sort)-1 and pointer2 <= len(list2)-1:
        if list1_sort[pointer1][0] == list2[pointer2]:  # if word of list1 is same as word of list2, record the corresponding index
            anagram_list.append(list1_sort[pointer1][1])
            if pointer1 == len(list1_sort) - 1: # if list1 has no more word to compare, exit the loop else compare next word
                break
            pointer1 += 1   
        elif list1_sort[pointer1][0] > list2[pointer2]: # if word of list1 is greater than word in reference list, upgrade word in list2
            if pointer2 == len(list2) - 1:  # if there is no more word to compare in list2, exit the loop else check next word in list2
                break
            pointer2 += 1
        else:   # if word in list1 is smaller than word in list2, there is no anagram for this word and move for next word in list1
            pointer1 += 1

    # find the original of the anagram words using index((word,index)) O(1) for list accessing using index and store them back to anagram_list.
    # Complexity: O(n) where n is the size of anagram_list and its worst case will be O(L1) if words of L1 is all anagram.
    count = 0    
    for index in anagram_list:  
        anagram_list[count] = list1[index][0]
        count += 1
        
    return anagram_list


"""
Function sort_counting_charinWord sort each and every character from its own word from the list input which contain tuple element(word,index)
The function will return the input_list where each and every character of the word in the list are sorted based on alphabetical ordering using couting sort.
Complexity will be O(L(M+W)) where L is the size of the list while M is the length of largest word among the list, W is the largest character among the word.
The input list is built with tuple. We need to sort for word only and return a list with tuple.
"""


def sort_counting_charinWord(input_list):
    new_list = []
    for word in input_list: 
        if word[0] == "":
            element = ("", word[1]) 
        else:
            max_char = word[0][0]  
            for char in word[0]:
                if char > max_char:
                    max_char = char
            max_char_length = int(ord(max_char))-97
            count_array_char = [None] * (max_char_length+1)
            for i in range(len(count_array_char)):
                count_array_char[i] = []
            for char in word[0]:
                count_array_char[int(ord(char[0]))-97].append(char)
            sorted_word = ""
            for x in range(len(count_array_char)):
                if count_array_char[x] != []:
                    # here we use .join method to concatenate alphabet since its complexity is O(M) where M is the length of the word
                    for y in range(len(count_array_char[x])):   
                        sorted_word = "".join([sorted_word,count_array_char[x][y]])
            element = (sorted_word, word[1])
        new_list.append(element)
    input_list = new_list
    return input_list
  

"""
Function sortbylength sort each and every word from the list input which contain tuple element(word,index)
The function will return the input_list where each and every words are sorted based on their length using couting sort and a max_item_length which record the length of longest word
Complexity will be O(L+G) where L is the size of the list while G is the longest length of word among the list.
The input list is built with tuple. We need to sort for word only and return a list with tuple and the max_item_length.
"""


def sortbylength(input_list):
    # sort item by length of each element
    max_item_length = len(input_list[0][0])
    for item in input_list:
        if len(item[0]) > max_item_length:
            max_item_length = len(item[0])
    count_array = [None] * (max_item_length + 1)
    for i in range(len(count_array)):
        count_array[i] = []
    for item in input_list:
        count_array[len(item[0])].append(item)
    index = 0
    for x in range(len(count_array)):
        if count_array[x] != []:
            for y in range(len(count_array[x])):
                input_list[index] = count_array[x][y]
                index += 1

    return input_list, max_item_length


"""
Function sort_radix_string sort each and every word from the list input which contain tuple element(word,index). The longest_length is the longest length of the word in the list
The function will return the input_list where each and every words are sorted with alphabetical ordering using radix sort.
Complexity will be O(LM) where L is the size of the list while M is the longest character among the list.
The input list is built with tuple. We need to sort for word only and return a list with tuple.
Since the preprocessing is included, the algorithm will start from last element to first element. The couting_array will also need to be applied in a reverse way.
"""


def sort_radix_string(input_list, longest_length):
    for col in range(longest_length-1, -1, -1):
        new_list = []
        greatest_char = input_list[-1][0][col]
        for x in range(len(input_list)-1, -1, -1):
            if len(input_list[x][0]) > col:
                if input_list[x][0][col] > greatest_char:
                    greatest_char = input_list[x][0][col]
            # pre processing
            else:
                break
        counting_array = [None] * ((int(ord(greatest_char))-96) + 1)
        for i in range(len(counting_array)):
            counting_array[i] = []
        for y in range(len(input_list)-1, -1, -1):
            if len(input_list[y][0]) > col:
                counting_array[int(ord(input_list[y][0][col]))-96].append(input_list[y])
            # pre processing
            else:
                for z in range(y, -1, -1):
                    counting_array[0].append(input_list[z])
                break
        for j in range(len(counting_array)):
            if counting_array[j] != []:
                for k in range(len(counting_array[j])-1, -1, -1):
                    new_list.append(counting_array[j][k])
        input_list = new_list
    return input_list
