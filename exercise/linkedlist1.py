class ListNode:
    def __init__(self, data):
        """constructor to initiate this project"""

        # store data
        self.data = data

        # store reference (next item)
        self.next = None
        return

    def has_value(self, value):
        """method to compare the value with the node data"""
        if self.data == value:
            return True
        else:
            return False


node1 = ListNode(15)
node2 = ListNode(8.2)
node3 = ListNode("Berlin")


class SingleLinkedList:
    def __init__(self):
        """constructor to initiate this object"""

        self.head = None
        self.tail = None
        return

    def add_list_item(self, item):
        """add an item at the end of the list"""

        if not isinstance(item, ListNode):
            item = ListNode(item)

        if self.head is None:
            self.head = item
        else:
            self.tail.next = item

        self.tail = item
        return

    def list_length(self):
        """returns the number of list items"""

        count = 0
        current_node = self.head

        while current_node is not None:
            # increase counter by one
            count = count + 1

            # jump to the linked node
            current_node = current_node.next

        return count

    def output_list(self):
        """outputs the list (the value of the node, actually)"""

        current_node = self.head

        while current_node is not Null:
            print(current_node.data)

            # jump to the linked node
            current_node = current_node.next

        return

