#pragma once

#include <list>

#include "Node.h"

using namespace std;

class Ring : public list <Node>
{
public:
	void push_back(Node& node)
	{
		if (!this->empty())
		{
			Node lastNode = this->back();
			lastNode.setNextNode(&node);
			list::push_back(node);

			Node firstNode = this->front();
			node.setNextNode(&firstNode);
		}
	}
};