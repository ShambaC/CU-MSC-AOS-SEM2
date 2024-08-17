#include <wx/wx.h>
#include <iostream>
#include <string>
#include <fstream>

#include "Node.h"

#define WinMain main

using namespace std;

/*
Container panel to hold all objects
*/
class Container : public wxPanel
{
public:
	Container(wxWindow* parent) : wxPanel(parent)
	{
		wxStaticText *testText = new wxStaticText(this, 1, "This is a sample text");
		//Node* node = new Node("A button", this, 2);
	}
};

/*
Frame to hold the panels
*/
class GUIFrame : public wxFrame
{
public:
	GUIFrame() : wxFrame(nullptr, wxID_ANY, "Token Ring", wxDefaultPosition, wxSize(900, 900))
	{
		initFrame();
	}

private:
	void initFrame()
	{
		Container container = new Container(this);
	}
};


/*
Application class
*/
class TokenApp : public wxApp
{
public:
	bool OnInit() override
	{
		ofstream out("outputLog.txt");
		cout.rdbuf(out.rdbuf());

		GUIFrame* frame = new GUIFrame();
		frame->Show(true);
		return true;
	}
};

wxIMPLEMENT_APP(TokenApp);