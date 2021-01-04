import java.util.Random;
import java.util.Scanner;

public class ClockTest {
    /**
     * @param args Clock页面置换算法
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        int size = 3;//内存块的大小（可以存放几个页面）
        int pagesize = 10;
        Scanner in = new Scanner(System.in);

        System.out.println("**************************************");
        System.out.println("*************改进clock算法***************");
        System.out.println("***说明：页面是否被修改，表示装入内存块的页面是否被***");
        System.out.println("******cpu修改了值。该值在本程序中可以自己决定*****");
        System.out.println("**************************************");

        System.out.println("请输入内存块的大小：");
        size = in.nextInt();
        block[] arr = new block[size];//声明一个包含size个页面的内存块
        for (int i = 0; i < size; i++) {
            arr[i] = new block();
        }


        System.out.println("请输入访问页面的容量");
        pagesize = in.nextInt();
        page[] pages = new page[pagesize];//存储要访问的页面，

        System.out.println("请选择操作(输入标号)：");
        System.out.println("1、页面访问序列(0~7)和是否被修改(0,1)都是用默认的");
        System.out.println("2、动态决定页面访问序列，是否被修改采用默认值");
        System.out.println("3、动态决定页面访问序列和是否被修改");
        int action = in.nextInt();

        Random random = new Random();
        if (action == 1) {
            for (int i = 0; i < pagesize; i++) {
                pages[i] = new page(random.nextInt(8), random.nextInt(10) % 2);//模拟页号为1的页面不会被修改
            }
        } else if (action == 2) {

            for (int i = 0; i < pagesize; i++) {
                System.out.println("请输入第" + i + "个页号");
                pages[i] = new page(in.nextInt(), random.nextInt(10) % 2);//模拟页号为1的页面不会被修改
            }
        } else if (action == 3) {
            for (int i = 0; i < pagesize; i++) {
                System.out.println("请输入第" + i + "个页号和是否被会被修改");
                pages[i] = new page(in.nextInt(), in.nextInt());//模拟页号为1的页面不会被修改
            }
        }

        System.out.println("页面号\t是与否被修改");
        for (page page : pages) {
            System.out.println(page.page + "\t" + page.modify);
        }

        //改进的页面置换算法
        clock(arr, pages);


    }

    //改进的clock算法
    public static void clock(block[] block, page[] page) {

        //用于表示当前页面是否装入内存成功
        boolean flag = false;

        //缺页数
        int count = 0;

        //模拟访问页面的序列
        for (int i = 0; i < page.length; i++) {

            for (int m = 0; m < block.length; m++) {
                System.out.println("页面号：" + block[m].page + ";  使用位：" + block[m].access + ";  修改位：" + block[m].modify);
            }

            System.out.println("将要访问的页面" + page[i].page);
            if (contain(block, page[i])) {
                //存在于内存块中，则不会产生缺页现象，继续执行外层，
                System.out.println("命中！");
                for (int m = 0; m < block.length; m++) {
                    System.out.println("页面号：" + block[m].page + ";  使用位：" + block[m].access + ";  修改位：" + block[m].modify);
                }
                System.out.println("***********分隔线************************");
                continue;
            } else {
                System.out.println("缺页...");
                count++;//如果内存块中不存在这个页面则产生一个缺页现象，寻找合适的置换页面
                flag = false;
            }

            //页面没有成功装入内存的时候
            while (!flag) {

                //第一步，寻找内存块中，使用位和修改位都为1的内存块，然后进行置换
                for (int j = 0; j < block.length; j++) {
                    if (block[j].access == 0 && block[j].modify == 0) {
                        //找到了可以置换的页面，进行置换。并且修改使用位
                        block[j].page = page[i].page;
                        block[j].access = 1;
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    //成功装入内存,则直接跳出循环，不再执行，否则执行第二步
                    break;
                }

                //第二步，寻找使用位为0，修改位为1的页面进行置换，并且在遍历内存块的过程中，把遍历过的页面的使用位置为0；
                for (int j = 0; j < block.length; j++) {

                    if (block[j].access == 0 && block[j].modify == 1) {
                        //找到了可以置换的页面，进行置换。并且修改使用位
                        block[j].page = page[i].page;
                        block[j].access = 1;
                        flag = true;
                        break;
                    } else {
                        block[j].access = 0;//修改使用位为0
                    }
                }
            }

            for (int m = 0; m < block.length; m++) {
                System.out.println("页面号：" + block[m].page + ";  使用位：" + block[m].access + ";  修改位：" + block[m].modify);
            }
            System.out.println("***********分隔线************************");
        }

        System.out.println("缺页次数：" + count);
        System.out.println("缺页率：" + (count * 1.0) / page.length);
    }


    //判断page页面是否存在于block内存块中，如果存在，则将其使用位置为1
    public static boolean contain(block[] block, page page) {

        for (int i = 0; i < block.length; i++) {
            //如果页面存在于内存块中，则修改使用位和修改位
            if (block[i].page == page.page) {
                block[i].page = page.page;
                block[i].access = 1;
                block[i].modify = page.modify;
                return true;
            }
        }
        return false;
    }

}

//内存空间类，用于表示一个内存空间单元
class block {
    int page = -1;//页面号，表示存储于该内存页的页号，默认为-1

    int access = 0;//使用标志位，默认值为0，表示未使用
    int modify = 0;//修改标志位，默认值为0，表示未修改
}

//页面类，表示要访问的页号
class page {
    public page(int page, int modify) {
        this.page = page;
        this.modify = modify;
    }

    int page = -1;//表示该页面包含的页号
    int modify = 0;//模拟该页面被装入内存块的时候，是否被修改，0表示不修改，1表示修改，默认值为0
}

