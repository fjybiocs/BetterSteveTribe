package org.stevetribe.betterstevetribe.itembuy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.stevetribe.betterstevetribe.BetterSteveTribe;
import org.stevetribe.betterstevetribe.utils.TimeUtil;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class ItemManager {
    private JavaPlugin context = null;
    private static ItemManager instance = null;
    private Map<String, SellableItem> sellableItems = new HashMap<>(); // 当日随机收购列表
    private BukkitScheduler scheduler = null;
    public static ItemManager getInstance() {
        if(instance == null) {
            instance = new ItemManager();
        }

        return instance;
    }


    /**
     * 初始化每日商店，注册定时刷新任务
     */
    public void init(JavaPlugin context) {
        this.context = context;
        this.scheduler = context.getServer().getScheduler();
        updateDailyShop();
        registerScheduledTask();
    }

    /**
     * 取消定时刷新任务
     */
    public void onDestory(){
        scheduler.cancelTasks(context);
        context = null;
        scheduler = null;
    }

    public Map<String, SellableItem> getSellableItems() {
        return sellableItems;
    }

    /**
     * 注册定时刷新任务，每天上午6点会刷新每日商店列表
     */
    private void registerScheduledTask() {
        // 获取当前时间
        Calendar now = Calendar.getInstance();

        // 计算距离下一个六点的时间差
        Calendar nextSixAM = Calendar.getInstance();
        nextSixAM.set(Calendar.HOUR_OF_DAY, 6);
        nextSixAM.set(Calendar.MINUTE, 0);
        nextSixAM.set(Calendar.SECOND, 0);

        if (now.after(nextSixAM)) {
            nextSixAM.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = nextSixAM.getTimeInMillis() - now.getTimeInMillis();
        scheduler.scheduleSyncRepeatingTask(context, new Runnable() {
            @Override
            public void run() {
                createDailyShopList();
            }
         }, delay, 24 * 60 * 60 * 20); // 24小时的时间间隔（以游戏刻为单位）
    }

    private void updateDailyShop() {
        // 根据规则获取当日的每日收购文件
        File configFile = new File(BetterSteveTribe.getPlugin().getDataFolder(), generateListFileName());

        // case：每日商店类表已生成直接解析
        if(configFile.exists()) {
            // 解析json文件，初始化

            String jsonContent = readJsonFile(configFile.getPath());
            Gson gson = new Gson();
            Type listType = new TypeToken<List<SellableItem>>() {}.getType();
            List<SellableItem> myList = gson.fromJson(jsonContent, listType);
            for (SellableItem obj : myList) {
                sellableItems.put(obj.getName(), obj);
            }
        } else {
            // case：每日商店类表未生成，创建列表
            createDailyShopList();
        }
    }

    /**
     * 判断每日列表是否生成
     * @return bool
     */
    private boolean isListGenerated() {
        File configFile = new File(BetterSteveTribe.getPlugin().getDataFolder(), generateListFileName());
        return configFile.exists();
    }

    /**
     * 生成每日商店列表
     */
    private void createDailyShopList(){
        File configFile = new File(BetterSteveTribe.getPlugin().getDataFolder(), "item-sell.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // 每日商店生成规则
        // 固定回收27种方块 & 物品
        // 包括随机矿物3种 其他方块24种
        sellableItems.clear();
        getItemByType(config, "high", 3);
        getItemByType(config, "low", 24);

        // 持久化当日列表
        saveShopList();
    }

    /**
     * 生成某类型方块的随机列表，结果保存在 sellableItems
     * @param config 配置文件，用于保存所有方块的候选列表
     * @param type 指定方块类型
     * @param nums 指定随机列表的长度
     */
    private void getItemByType( YamlConfiguration config, String type, int nums) {
        if(config.contains(type)) {
            List<Object> itemList = (List<Object>) config.getList(type);
            if(itemList == null) {
                return;
            }
            // 随机获取4种item
            int[] idx = generateRandomArray(itemList.size(), nums);
            for(int i = 0 ; i < idx.length; i++){
                Object itemObject = itemList.get(idx[i]);
                if (itemObject instanceof Map) {
                    Map<String, Object> itemMap = (Map<String, Object>) itemObject;
                    String name = (String) itemMap.get("name");
                    name = name.toUpperCase();

                    double price = (Double) itemMap.get("price");
                    int limit = (Integer) itemMap.get("limit");

                    SellableItem sellableItem = new SellableItem(name, price, limit);
                    sellableItems.put(sellableItem.getName(), sellableItem);
                }
            }
        }
    }

    /**
     * 生成随机index的数组
     * @param m 输入长度为m
     * @param n 返回数组长度
     * @return 返回一个数组，长度为n，数组元素为取值范围[0, m)的随机递增数组
     */
    private int[] generateRandomArray(int m, int n){
        if (m <= 0 || n <= 0 || n > m) {
            throw new IllegalArgumentException("Invalid input range or length");
        }

        int[] result = new int[n];
        Set<Integer> generatedNumbers = new HashSet<>();
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < n; i++) {
            int randomNumber;
            do {
                randomNumber = random.nextInt(m);
            } while (generatedNumbers.contains(randomNumber));

            generatedNumbers.add(randomNumber);
            result[i] = randomNumber;
        }

        return result;
    }

    /**
     * 保存每日列表
     * todo 改为异步
     */
    private void saveShopList(){
        File outputFile = new File(BetterSteveTribe.getPlugin().getDataFolder(), generateListFileName());
        ArrayList<SellableItem> list = new ArrayList<SellableItem>(sellableItems.values());
        Gson gson = new Gson();

        // 将容器对象转换为 JSON 字符串
        String jsonString = gson.toJson(list);

        // 将 JSON 字符串写入文件
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(jsonString);
            // System.out.println("容器对象已成功序列化为 JSON 文件。");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readJsonFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(filePath)) {
            int character;
            while ((character = reader.read()) != -1) {
                content.append((char) character);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 获取当日的每日列表文件名称
     * e.g item-list-112374876987500.yml
     * @return string
     */
    private String generateListFileName() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
        long timestamp = TimeUtil.string2timestamp(format.format(date));

        return "item-list-" + timestamp + ".json";
    }

    private ItemManager() {}
}