#pragma once

#include <functional>
#include <chrono>
#include <future>
#include <cstdio>

using namespace std;

/*
* This class is used to create a timer and call a function after given time.
* Usage: later laterObj(isRepeating)
*		 laterObj.start(delay, isAsync, function, args...);
*/
class later
{
private:
	bool isRepeats = false;

public:
	later(bool isRepeats)
	{
		this->isRepeats = isRepeats;
	}

	template <class callable, class... arguments>
	void start(int after, bool async, callable&& f, arguments&&... args)
	{
		function<typename result_of<callable(arguments...)>::type()> task(bind(forward<callable>(f), forward<arguments>(args)...));

		do
		{
			if (async)
			{
				thread([after, task]()
					{
						this_thread::sleep_for(chrono::milliseconds(after));
						task();

					}).detach();
			}
			else
			{
				this_thread::sleep_for(chrono::milliseconds(after));
				task();
			}
		} while (isRepeats);
	}

	void stop()
	{
		this->isRepeats = false;
	}
};