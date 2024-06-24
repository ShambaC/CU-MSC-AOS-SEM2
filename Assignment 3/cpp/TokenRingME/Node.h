#pragma once

#include <wx/wx.h>
#include <thread>
#include <mutex>
#include <iostream>
#include <cstdlib>

#include "Token.h"
#include "Later.h"

using namespace std;

class Node : public wxButton
{
private:
	Node *nextNode;

	bool isRequestSent = false;

	void onClick(wxCommandEvent& event)
	{
		if (!isRequestingCS && !isInCS)
		{
			cout << "\nNode " << nodeId << " is requesting for CS";
			isRequestingCS = true;
			SetBackgroundColour(wxColour(201, 192, 133));
			SetOwnForegroundColour(wxColour(*wxBLACK));
		}
	}

	void startCS()
	{
		SetBackgroundColour(wxColour(166, 88, 76));
		SetOwnForegroundColour(wxColour(*wxWHITE));

		srand((unsigned)time(NULL));
		int randomDelay = 2 + (rand() % 10);

		cout << "\nNode " << nodeId << " going into CS for " << randomDelay << "s\n";

		int remainingTime = randomDelay;
		auto countDownMethod = [&](void)
			{
				remainingTime--;
				SetLabel(nodeId + ", " + to_string(remainingTime));
			};

		later countDownTimer(true);
		countDownTimer.start(1000, true, &countDownMethod);

		auto afterCS = [&](void)
			{
				countDownTimer.stop();
				SetLabel(nodeId);
				stopCS();
			};

		later CSTimer(false);
		CSTimer.start(randomDelay * 1000, true, &afterCS);
	}

	void stopCS()
	{
		cout << "\nNode " << nodeId << " is done with CS";
		isInCS = false;

		SetBackgroundColour(wxColour(185, 235, 255));
		SetOwnForegroundColour(wxColour(*wxBLACK));

		if (!token->nodeQueue.empty())
		{
			Node nextInQueue = token->nodeQueue.front();
			token->nodeQueue.pop();

			cout << "\nPassing the token to the next node " << nextNode->nodeId;
			token->setNodeId(nextInQueue.nodeId);
			isPHold = false;
			nextNode->sendToken(*token);
		}
	}

public:
	string nodeId;
	bool isPHold;
	Token* token;

	bool isRequestingCS = false;
	bool isInCS = false;

	mutex m;

	Node(string id, wxWindow* parent, int objectId) : wxButton(parent, objectId, id)
	{
		this->nodeId = id;
		this->isPHold = false;
		this->token = nullptr;
		this->nextNode = nullptr;

		Bind(wxEVT_BUTTON, &Node::onClick, this, objectId);
	}

	Node(const Node& node)
	{
		nodeId = node.nodeId;
		isPHold = node.isPHold;
		token = node.token;
		nextNode = node.nextNode;

		Bind(wxEVT_BUTTON, &Node::onClick, this, this->GetId());
	}

	void setNextNode(Node* node)
	{
		nextNode = node;
	}

	Node* getNextNode()
	{
		return nextNode;
	}

	void sendToken(Token recievedToken)
	{
		if (_strcmpi(recievedToken.nodeId.c_str(), this->nodeId.c_str()) == 0)
		{
			cout << "\nRecieved token at Node " << nodeId << ", this is the required destination. Making it p_hold";
			this->isPHold = true;
			this->token = &recievedToken;
			this->token->setIsAtLocation(true);

			// TODO: set screen coords for the token here
		}
		else
		{
			cout << "\nRecieved token at Node " << nodeId << ", not the required destination, forwarding token to Node " << this->nextNode->nodeId;
			recievedToken.setIsAtLocation(false);
			nextNode->sendToken(recievedToken);
		}
	}

	void sendRequest(Node* node)
	{
		cout << "\nNode" << nodeId << " has recieved a request from Node " << node->nodeId;

		if (this->isPHold)
		{
			if (isInCS)
			{
				if (_strcmpi(nodeId.c_str(), node->nodeId.c_str()) == 0)
				{
					cout << "\nNode " << nodeId << " is in CS, adding the request of Node " << node->nodeId << " to the queue";
					token->nodeQueue.push(*node);
				}
			}
			else
			{
				cout << "\nNode " << nodeId << " is p_hold but not in the CS, forwarding the token to next Node " << this->nextNode->nodeId << ", destination : Node " << node->nodeId;
				token->setNodeId(node->nodeId);
				this->isPHold = false;
				this->nextNode->sendToken(*token);
			}
		}
		else
		{
			if (this->nextNode != nullptr)
			{
				cout << "\nNode " << nodeId << " is not the p_hold, forwarding the request to Node " << this->nextNode->nodeId;
				this->nextNode->sendRequest(node);
			}
		}
	}

	void run()
	{
		while (true)
		{
			m.lock();
			if (isRequestingCS)
			{
				if (isPHold)
				{
					isRequestingCS = false;
					isRequestSent = false;
					isInCS = true;
					startCS();
				}
				else if (!isRequestSent)
				{
					this->nextNode->sendRequest(this);
					isRequestSent = true;
				}
			}
			m.unlock();
		}
	}
};