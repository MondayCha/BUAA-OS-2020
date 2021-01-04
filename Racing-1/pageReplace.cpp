// pageReplace.cpp

#include "pageReplace.h"            // 测评需求，请务必包含该头文件
#define MAX_PHY_PAGE 64             // 使用了64个物理页框
#define MAX_PAGE 12                 // 页面大小为4KB
#define get_Page(x) (x >> MAX_PAGE) // 虚拟地址转换为页框号

void pageReplace(long *physic_memery, long nwAdd)
{
    bool flag = false;
    static int clock = 0;
    static bool visitBit[MAX_PHY_PAGE] = {0};

    // 如果在内存中能够找到要访问的页面，返回
    for (int i = 0; i < MAX_PHY_PAGE; i++)
    {
        if ((nwAdd >> MAX_PAGE) == physic_memery[i])
        {
            visitBit[i] = true;
            // 当该页随后再被访问到时，它的使用位也被置为1
            return;
        }
    }

    // 查找空闲页面，若存在将flag置为1
    for (int i = 0; i < MAX_PHY_PAGE; i++)
    {
        if (physic_memery[i] == 0)
        { //初始物理页框内容全部为0
            physic_memery[i] = get_Page(nwAdd);
            visitBit[i] = true;
            // 当某一页首次装入主存时，该帧的使用位设置为1
            flag = true;
            break;
        }
    }

    // 若不存在空闲页面
    if (!flag)
    {
        while (!flag)
        {
            if (visitBit[clock] == true)
            {
                visitBit[clock] == false;
                clock = (clock + 1) % MAX_PHY_PAGE;
            }
            else
            {
                physic_memery[clock] = get_Page(nwAdd);
                clock = (clock + 1) % MAX_PHY_PAGE;
                break;
            }
        }
    }
}