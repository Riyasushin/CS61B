# Gitlet Design Document

**Name**: rj

## Questions

How to ADD???

## Classes and Data Structures

### Main

用来读取命令参数，调用各命令

#### Fields

1. Field 1
2. Field 2


### Repository

Gitlet的实际参数、方法，放各种实现供调用

#### Fields

1. ✅File CWD: 
    当前工作的文件夹

2. ✅File GITLET_DIR:
    .gitlet的文件夹

#### Methods

1. ✅Boolean hasInited()
    True    : 初始化过了
    False   :
    初始化的判断标准：有没有.gitlet文件夹

2. Boolean init()
    进行初始化，创建文件夹
    只能在没有初始化时候调用
    True: 创建成功
    - new dir
    - new branch
    - new commit


### Commit

#### Fields

1. String id
2. Time date
3. String message
4. Commit[] parents

#### Methods

1. String toString() / void info()

   - ===
     commit [id]

     Merge: [parent1] [parent2]

     Date: Thu Nov 9 20:00:05 2017 -0800

     [A commit message.]
2. Commit()

   初始化



### GitletException



#### Fields



#### Methods

1. static void checkOfOperands(final int realArgNum, final int... args)

   参数不对的错误

   对不上就exit

   

2. 




## 逻辑流程
这部分需要详细描述系统中使用的核心算法，例如提交算法、合并算法、查找算法等。

### 读取输入

✅没输参数: Please enter a command.
    exit

✅命令不存在 -> `switch`的 `default`:No command with that name exists.

### init

✅检查参数对不对： Incorrect operands.

✅检查是否初始化过（看有无文件夹中特定文件存在存在)
    True: Error()
    False: Create .gitlet文件夹
            new branch:master
            commit 空, time:0

### add

✅检查参数对不对

✅检查初始化过没有！   Not in an initialized Gitlet directory.

✅检查文件存在与否！	File does not exist.

检查和暂存区的相同不相同（有无、内容有变化吗）

- Suggested Lecture(s): Lecture 16 (Sets, Maps, ADTs), Lecture 19 (Hashing)

### commit

### rm

检查参数对不对

检查初始化了吗

检查存在暂存区了吗Unstage the file if it is currently staged for addition.

检查在head commit里面追踪了吗 stage it for removal and remove the file from the working directory if the user has not already done so

都没有： `No reason to remove the file.`

**Dangerous?** Yes

### log

检查参数

检查init

输出所有commit的信息直到initial commit

树的遍历

### global-log

### status

检查参数

检查init

输出branches的信息，标注当前branch，按字典序

输出stage区的信息

- Staged Files
- Removed Files
- Modifications Not Staged For Commit
  - Tracked in the current commit, changed in the working directory, but not staged; or
  - Staged for addition, but with different contents than in the working directory; or
  - Staged for addition, but deleted in the working directory; or
  - Not staged for removal, but tracked in the current commit and deleted from the working directory.
- Untracked Files
  - 存在于工作目录中，但既未被添加也未被跟踪的
  - 已暂存待删除，但在 Gitlet 不知情的情况下又重新创建的
  - 忽略任何可能被引入的子目录？？？？



### checkout

检查参数

检查init

1. `java gitlet.Main checkout -- [file name]`
2. `java gitlet.Main checkout [commit id] -- [file name]`
3. `java gitlet.Main checkout [branch name]`

分三种情况写

### branch

检查参数

检查init

检查名称重复！A branch with that name already exists.

Creates a new branch with the given name

points it at the current head commit



Hint： A branch is nothing more than a name for a reference (a SHA-1 identifier) to a commit node.

### rm-branch

### reset

### merge



## Algorithm



## Persistence
持久化部分主要说明系统如何将数据存储到磁盘上，以便在程序关闭后数据不会丢失。
