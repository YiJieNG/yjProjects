"""31158145 YI JIE NG FIT2004 Assignment 2"""

"""
# Task 1
Function best_schedule takes input of weekly_income which store a list of money that can be earned at each week,
competitions which store a list of tuple(start week, end week, money earned). 
The function should return the most money that can be earned based on the best schedule constructed.
Run time complexity: O(Nlog(N)) for sorting the merged list from two lists input where N is length of the merged list.
Space complexity: O(N) where N is the length of the merged list or length of the memo list .
"""


def best_schedule(weekly_income, competitions):
    # if weekly_income is empty list means the there is no money earned for every weeks and it always return 0
    if len(weekly_income) == 0:
        return 0

    # a base case tuple for week 0 is assigned where it start from week 0 and ends in week 0 provides 0 money to earn.
    my_list = [(0, 0, 0)]

    # create a new list(income_list) which merge both list.
    # append first list(weekly income) to the income_list and create their own start time and end time since tuple data
    # type is needed for future use. Complexity: O(w) where w is the length of weekly_income list since append.
    for x in range(len(weekly_income)):
        my_tuple = (x+1, x+1, weekly_income[x])
        my_list.append(my_tuple)
    income_list = my_list

    # append second list(competitions) to the income_list and increment their start time and end time since there is a
    # base case added before. Complexity: O(c) where c is the length of competitions list since append.
    for y in range(len(competitions)):
        my_tuple = ((competitions[y][0]+1), competitions[y][1]+1, competitions[y][2])
        income_list.append(my_tuple)

    # python default sorting method is used and it is a Tim sort which having complexity of O(N(log(N))
    # key is used to determine the item to be sort from a tuple
    # since every time the function call will return income_list[first item], index access will have O(1) complexity
    def take_second(income_list):
        return income_list[1]

    # sort the income_list by using Tim sort with the key of starting time
    income_list.sort(key=take_second)

    # create the memo list in order to memorise the previous optimal decision
    memo = [0] * (len(weekly_income) + 1)

    # starting from week 1 to last week, complexity: O(N) where N is the size of income_list
    for z in range(1, len(income_list)):
        # if current week is starting and ending time and memo[current week] < profit[current week] + memo[last_week]
        if income_list[z][0] == income_list[z][1] and memo[income_list[z][1]] < income_list[z][2] + memo[(income_list[z][1])-1]:
            # current week optimal profit = current week money earned + optimal money earned last week
            memo[income_list[z][1]] = income_list[z][2] + memo[(income_list[z][1])-1]
        # else it is competition case
        # if money earned for competition > money earned for normal job(ending - starting time of competition)
        elif (memo[income_list[z][1]] - memo[(income_list[z][0])-1]) < income_list[z][2]:
            # current week optimal profit = competition money earned + optimal money earned last week
            memo[income_list[z][1]] = income_list[z][2] + memo[(income_list[z][0])-1]

    # the final week profit is the optimal total money earned
    return memo[-1]


"""
# Task 2
Function best_itinerary takes input of profit which is a list of list that store the profit that can be earned for each 
city at each day. Column(d) = profit for each city, Row(c) = profit for each day. Size = d x c.
Quarantine time is a list which store the quarantine day requirement for each city. home is the starting base for day 1.
The function should return the optimal profit from day 1 to final day starting from their home base(travel/work at day1)
Run time complexity: O(nd) where n is number of cities and d is number of days.
Space complexity: O(nd) where n is number of cities and d is number of days.
"""


def best_itinerary(profit, quarantine_time, home):
    # create 2x2 matrix memo to record optimal profit using "[[None for range] for range]" to avoid list reference error
    # complexity will be O(nd) as well where n is number of cities and d is number of days.
    # (None, None) is assigned where first value represent the optimal profit while second value represent its direction
    # For second value, None = unassigned, 0 = stay at own city, 1 = travel to other city, -1 = invalid
    memo = [[(None, -1) for _ in range(len(profit[0]))] for _ in range(len(profit)+1)]

    # last day(base case) which is day d+1 is assigned as (0, 0) for all city
    for i in range(len(profit[0])):
        memo[-1][i] = (0, 0)

    # find the earliest start working day for each city according to the home base
    # O(nk) complexity where n is the number of cities, k is the total travel + quarantine days starting from home base.
    for j in range(len(quarantine_time)):
        # if current city is not the home,
        if j != home:
            # calculate the travel day from home to the city
            travel = abs(home - j)
            # calculate the earliest start working day for the city
            earliest_start = travel + quarantine_time[j]
            # if the travel or earliest start working day exists the maximum working day, the profit of that city will
            # be 0 everyday and -1 for direction since it is impossible to stay at this city.
            if travel >= len(memo) or earliest_start >= len(memo):
                for k in range(len(memo)):
                    memo[k][j] = (0, -1)
            else:
                # (0, -1) is assigned where 0 profit from first day to the earliest starting day -1 and -1 for direction
                # since it is impossible to stay at this city.
                for k in range(earliest_start):
                    memo[k][j] = (0, -1)

    # Starting from last day(exclude day d+1) to the first day, there are 5 possible cases could be face for each city.
    # The best optimal for the city and the specific day will be maximum profit earned from these 5 cases.
    # Complexity will be O(nd) where n is number of cities and d is number of days.
    # (P, D) is stored for each city at specific day where P = optimal profit, D = 0 if stay at its own city else 1.
    for d in range(len(memo)-2, -1, -1):
        for c in range(len(memo[0])):
            # case 1) stay at own city and work.
            # if it is a valid day to work,
            # (profit, direction) = (profit of current day + last day optimal profit earned, 0)
            if memo[d][c][0] is None:
                case1 = (profit[d][c] + memo[d+1][c][0], 0)
            else:
                # else case 1 is ignored since invalid
                case1 = (-1, -1)

            # case 2) travel to right adjacent city and stay to work.
            # if c is at right most position, case2 is ignored.
            if c == len(memo[0])-1 or quarantine_time[c+1] >= len(profit)-1-d:
                case2 = (-1, -1)
            else:
                # (profit, direction) = (profit of right adjacent city after finish quarantine, 1)
                case2 = (memo[quarantine_time[c+1]+1+d][c+1][0], 1)

            # case 3) travel to left adjacent city and stay to work.
            # if c is at left most position, case3 is ignored.
            if c == 0 or quarantine_time[c-1] >= len(profit)-1-d:
                case3 = (-1, -1)
            else:
                # (profit, direction) = (profit of left adjacent city after finish quarantine, 1)
                case3 = (memo[quarantine_time[c-1]+1+d][c-1][0], 1)
            
            # case 4) travel to another city(exclude adjacent city) at right to stay and work.
            # if c is at right most position or optimal profit of right adjacent city at previous day is from its own
            # city means travel is unnecessary, case4 is ignored.
            if c == len(memo[0])-1 or memo[d+1][c+1][1] == 0:
                case4 = (-1, -1)
            else:
                # (profit, direction) = (profit of travel to another city at right, 1)
                case4 = (memo[d+1][c+1][0], 1)

            # case 5) travel to another city(exclude adjacent city) at left to stay and work.
            # if c is at left most position or optimal profit of left adjacent city at previous day is from its own
            # city means travel is unnecessary, case5 is ignored
            if c == 0 or memo[d+1][c-1][1] == 0:
                case5 = (-1, -1)
            else:
                # (profit, direction) = (profit of travel to another city at left, 1)
                case5 = (memo[d+1][c-1][0], 1)

            # choose the max profit earned from each case and assign the tuple to the memo[d][c]
            # complexity will be O(c) where c is the total cases.
            memo[d][c] = max(case1, case2, case3, case4, case5)

    return memo[0][home][0]
