#pragma once

#include <string>
#include <queue>

class Node;

using namespace std;

/*
 This class represents a token in the mutual exclusion algorithm.
 The node holding the token is known as the pHold.
*/
class Token
{
private:
	bool isAtLocation = false;

public:
	string nodeId;
	queue<Node> nodeQueue;

	Token()
	{
		nodeId = "";
	}

	Token setNodeId(string nodeId)
	{
		this->nodeId = nodeId;
		return *this;
	}

	bool getIsAtLocation()
	{
		return isAtLocation;
	}
	void setIsAtLocation(bool value)
	{
		isAtLocation = value;
	}

	/*friend ostream& operator << (ostream& outs, const Token* token)
	{
		string outString = "\n\n<----TOKEN---->\n";

		outString += token->isAtLocation ? "Current location: Node " + token->nodeId : "Target Location: Node " + token->nodeId;
		outString += "\nQueue: [";

		queue<Node> tmp_q = token->nodeQueue;

		while (!tmp_q.empty())
		{
			Node currentNode = tmp_q.front();
			outString += "Node " + currentNode.nodeId + ", ";
			tmp_q.pop();
		}

		outString += "]\n\n";

		outs << outString;
	}*/
};