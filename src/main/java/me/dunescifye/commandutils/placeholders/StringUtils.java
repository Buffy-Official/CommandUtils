package me.dunescifye.commandutils.placeholders;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.dunescifye.commandutils.CommandUtils;
import me.dunescifye.commandutils.utils.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends PlaceholderExpansion {

    private static String separator = ",", elseIfKeyword, elseKeyword, conditionSeparator;
    private static boolean allowCustomSeparator;

    public StringUtils(CommandUtils plugin, YamlDocument config) {
        Logger logger = plugin.getLogger();
        if (config.isString("Placeholders.If.ElseIfKeyword")) {
            elseIfKeyword = config.getString("Commands.If.ElseIfKeyword");
            if (elseIfKeyword == null)
                config.set("Commands.If.ElseIfKeyword", "elseif");
        } else {
            logger.warning("Configuration Commands.If.ElseIfKeyword is not a String. Using default value of `elseif`");
            elseIfKeyword = "elseif";
        }

        if (config.isString("Commands.If.ElseKeyword")) {
            elseKeyword = config.getString("Commands.If.ElseKeyword");
            if (elseKeyword == null)
                config.set("Commands.If.elseKeyword", "else");
        } else {
            logger.warning("Configuration Commands.If.ElseKeyword is not a String. Using default value of `else`");
            elseKeyword = "else";
        }

        if (config.isString("Commands.If.ConditionSeparator")) {
            conditionSeparator = config.getString("Commands.If.ConditionSeparator");
            if (conditionSeparator == null)
                config.set("Commands.If.ConditionSeparator", "\\\"");
        } else {
            logger.warning("Configuration Commands.If.ConditionSeparator is not a String. Using default value of `\"`");
            conditionSeparator = "\"";
        }
    }

    public static String getSeparator() {
        return separator;
    }

    public static void setSeparator(String separator) {
        StringUtils.separator = separator;
    }

    public static boolean isAllowCustomSeparator() {
        return allowCustomSeparator;
    }

    public static void setAllowCustomSeparator(boolean allowCustomSeparator) {
        StringUtils.allowCustomSeparator = allowCustomSeparator;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "stringutils";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DuneSciFye";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String args) {
        String function, arguments = null;

        String[] parts = args.split("(?<!\\\\)_", 2);
        function = parts[0];

        if (parts.length == 2) {
            arguments = PlaceholderAPI.setBracketPlaceholders(player, parts[1]);
        }


        for (int v = 0; v<2;v++){
            switch (function){
                case "inputoutput":
                    String[] split = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    int length = split.length;
                    if (length>1){
                        for (int i = 0; i<(length-1)/2; i++) {
                            if (Objects.equals(split[0], split[1 + i * 2])) {
                                return split[2 + i * 2];
                            }
                        }
                        return length%2==1 ? "" : split[length-1];
                    }
                case "inputoutputcycle":
                    String[] splitInputOutputCycle = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    int lengthInputOutputCycle = splitInputOutputCycle.length;
                    if (lengthInputOutputCycle>1){
                        for (int i = 0; i<(lengthInputOutputCycle-1); i++) {
                            if (Objects.equals(splitInputOutputCycle[0], splitInputOutputCycle[1 + i])) {
                                return splitInputOutputCycle[2 + i];
                            }
                        }
                        return lengthInputOutputCycle%2==1 ? "" : splitInputOutputCycle[lengthInputOutputCycle-1];
                    }
                case "randomint":
                    String[] partsRandomInt = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    int[] randomint = new int[partsRandomInt.length];

                    try {
                        for (int i = 0; i < partsRandomInt.length; i++){
                            randomint[i] = Integer.parseInt(parts[i]);
                        }
                    } catch (NumberFormatException e){
                        return "Only integer numbers are allowed.";
                    }

                    int min = Math.min(randomint[0],randomint[1]);
                    int max = Math.max(randomint[0],randomint[1]);

                    Random random = new Random();

                    if (randomint.length==3){
                        random = new Random(randomint[2]);
                    }
                    return String.valueOf(random.nextInt((max - min + 1)) + min);
                case "randomdouble":
                    arguments = "_" + arguments;
                    if (!arguments.contains("_min:")||!arguments.contains("_max:")){
                        return "Invalid arguments. Missing min or max.";
                    }
                    String regexDouble = "(_seed:|_round:|_min:|_max:)(.+?)(?=(_seed:|_round:|_min:|_max:|$))";

                    Pattern patternDouble = Pattern.compile(regexDouble);
                    Matcher matcherDouble = patternDouble.matcher(arguments);

                    double minDouble = 0, maxDouble = 0;
                    long seedDouble = 0;
                    int round = 2;

                    while (matcherDouble.find()) {
                        String key = matcherDouble.group(1);
                        String value = matcherDouble.group(2);

                        switch (key) {
                            case "_seed:":
                                seedDouble = Long.parseLong(value);
                                break;
                            case "_min:":
                                minDouble = Double.parseDouble(value);
                                break;
                            case "_max:":
                                maxDouble = Double.parseDouble(value);
                                break;
                            case "_round:":
                                round = Integer.parseInt(value);
                                break;
                        }
                    }
                    Random randomDouble = new Random();
                    if (seedDouble!=0){
                        randomDouble = new Random(seedDouble);
                    }
                    return String.format("%." + round + "f", randomDouble.nextDouble((maxDouble - minDouble)) + minDouble);
                case "randomstring":

                    Pattern patternRandomString = Pattern.compile("^(\\d+)_(.*)$");  //If arguments starts with double, use double as seed
                    Matcher matcherRandomString = patternRandomString.matcher(arguments);
                    Random randomString = new Random();

                    if (matcherRandomString.find()) {
                        arguments = matcherRandomString.group(2);
                        randomString = new Random(Long.parseLong(matcherRandomString.group(1)));
                    }

                    String[] splitRandom = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    return splitRandom[randomString.nextInt(splitRandom.length)];
                case "weightedrandomstring":

                    Pattern patternWeightedRandomString = Pattern.compile("^(\\d+)_(.*)$");  //If arguments starts with double, use double as seed
                    Matcher matcherWeightedRandomString = patternWeightedRandomString.matcher(arguments);
                    Random randomWeightedString = new Random();

                    if (matcherWeightedRandomString.find()) {
                        arguments = matcherWeightedRandomString.group(2);
                        randomWeightedString = new Random(Long.parseLong(matcherWeightedRandomString.group(1)));
                    }

                    String[] splitWeightedRandom = arguments.split(separator);
                    List<String> splitWeightedRandom2 = new ArrayList<String>();

                    for (int i = 0; i < splitWeightedRandom.length; i+=2){
                        for (int j = 0; j < Integer.parseInt(splitWeightedRandom[i+1]); j++){
                            splitWeightedRandom2.add(splitWeightedRandom[i]);
                        }
                    }
                    return splitWeightedRandom2.get(randomWeightedString.nextInt(splitWeightedRandom2.size()));
                case "changecolor":
                    String[] argsChangeColor = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (argsChangeColor.length < 2) {
                        return "Missing arguments!";
                    }
                    String input = argsChangeColor[0];
                    String newColor = argsChangeColor[1];
                    String regexColor = "(?i)\\b(WHITE|LIGHT_GRAY|GRAY|BLACK|BROWN|RED|ORANGE|YELLOW|LIME|GREEN|CYAN|LIGHT_BLUE|BLUE|PURPLE|MAGENTA|PINK)(_(\\w+))";
                    if (input.matches(regexColor)){
                        return input.replaceAll(regexColor, newColor + "$2");
                    } else {
                        if (input.equalsIgnoreCase("GLASS")) input = "STAINED_" + input;
                        return newColor + "_" + input;
                    }
                case "changewood":
                    String[] argsChangeWood = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (argsChangeWood.length < 2) {
                        System.out.println("Missing arguments.");
                    }

                    String inputChangeWood = argsChangeWood[0];
                    String newWood = argsChangeWood[1];

                    if (newWood.equalsIgnoreCase("stripped") && !inputChangeWood.contains("stripped")){
                        return "stripped_" + inputChangeWood;
                    } else if (newWood.isBlank()){
                        return inputChangeWood;
                    } else {
                        Pattern patternChangeWood = Pattern.compile("(dark_oak_|oak_|jungle_|acacia_|birch_|spruce_|mangrove_|cherry_|bamboo_|warped_|crimson_)");
                        Matcher matcherChangeWood = patternChangeWood.matcher(inputChangeWood);
                        if (matcherChangeWood.find()){
                            return matcherChangeWood.group(1) + newWood;
                        } else {
                            return inputChangeWood;
                        }
                    }
                case "replace":
                    String[] argsReplace = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (argsReplace.length < 3) {
                        return "Invalid arguments!";
                    }

                    if (argsReplace.length == 3) return argsReplace[0].replace(argsReplace[1], argsReplace[2]);
                    else if (argsReplace[3].equalsIgnoreCase("upper") || argsReplace[3].equalsIgnoreCase("uppercase")) return argsReplace[0].toUpperCase().replace(argsReplace[1].toUpperCase(), argsReplace[2].toUpperCase());
                    else if (argsReplace[3].equalsIgnoreCase("lower") || argsReplace[3].equalsIgnoreCase("lowercase")) return argsReplace[0].toLowerCase().replace(argsReplace[1].toLowerCase(), argsReplace[2].toLowerCase());

                    return argsReplace[0].replace(argsReplace[1], argsReplace[2]);
                case "multireplace":
                    String[] argsMultiReplace = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (argsMultiReplace.length < 3) {
                        return "Invalid arguments!";
                    }
                    String outputMultiReplace = argsMultiReplace[0];
                    for (int i = 0; i<(argsMultiReplace.length-1)/2; i++) {
                        outputMultiReplace = outputMultiReplace.replaceAll(argsMultiReplace[1 + i * 2], argsMultiReplace[2 + i * 2]);
                    }
                    return outputMultiReplace;
                case "executein":
                    String defaultOverworld = Bukkit.getWorlds().get(0).getName();
                    String defaultNether = Bukkit.getWorlds().get(1).getName();
                    String defaultEnd = Bukkit.getWorlds().get(2).getName();

                    if (arguments.equals(defaultOverworld)){
                        return "overworld";
                    } else if (arguments.equals(defaultNether)){
                        return "the_nether";
                    } else if (arguments.equals(defaultEnd)){
                        return "the_end";
                    } else {
                        return arguments.toLowerCase();
                    }
                case "nbt":
                    String[] argsNbt = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    ItemStack item = argsNbt[0].equals("-1") ? player.getPlayer().getInventory().getItemInMainHand() : player.getPlayer().getInventory().getItem(Integer.parseInt(argsNbt[0]));
                    if (item != null){
                        if (item.hasItemMeta()){
                            NamespacedKey keyEiID = new NamespacedKey(argsNbt[1], argsNbt[2]);
                            if (item.getItemMeta().getPersistentDataContainer().has(keyEiID)){
                                try {
                                    return item.getItemMeta().getPersistentDataContainer().get(keyEiID, PersistentDataType.STRING);
                                } catch (IllegalArgumentException e){
                                    return String.valueOf(item.getItemMeta().getPersistentDataContainer().get(keyEiID, PersistentDataType.DOUBLE));
                                }
                            }
                        }
                    }
                    return "";
                case "isgliding":
                    if (Objects.requireNonNull(player.getPlayer()).isGliding()){
                        return "yes";
                    } else {
                        return "no";
                    }
                case "cursoritem":
                    String[] argsCursorItem = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (argsCursorItem == null){
                        return String.valueOf(player.getPlayer().getItemOnCursor().getType());
                    } else if (argsCursorItem[0].equals("amt") || argsCursorItem[0].equals("amount")) {
                        return String.valueOf(player.getPlayer().getItemOnCursor().getAmount());
                    }

                    return String.valueOf(player.getPlayer().getItemOnCursor().getType());
                case "inventoryinfo":
                    //Requires: Slot, info type
                    String[] inventoryInfoArgs = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);

                    if (inventoryInfoArgs == null || inventoryInfoArgs.length < 2) return "Invalid arguments";

                    String invSlot = inventoryInfoArgs[0];

                    ItemStack itemStack = switch (invSlot) {
                        case "-1" ->
                            player.getPlayer().getInventory().getItemInMainHand();
                        case "cursor" ->
                            player.getPlayer().getItemOnCursor();
                        default ->
                            player.getPlayer().getInventory().getItem(Integer.parseInt(invSlot));
                    };

                    if (itemStack == null) return "null";
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    String infoType = inventoryInfoArgs[1];

                    switch (infoType){
                        case "armortrim":
                            if (itemMeta instanceof ArmorMeta armorMeta){
                                if (armorMeta.hasTrim()) return armorMeta.getTrim().getPattern().getKey().getKey();
                            }
                            return "null";
                        case "material":
                            return itemStack.getType().toString();
                        case "amount":
                            return String.valueOf(itemStack.getAmount());
                        default:
                            return "Invalid infotype";
                    }

                case "slottovanilla":

                    int slot = Integer.parseInt(arguments);
                    //Converts bukkit slot numbers to vanilla slot text
                    if (slot < 9){
                        return "hotbar." + slot;
                    } else {
                        return "inventory." + (slot - 9);
                    }

                case "blockat":
                    String[] blockatargs = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (blockatargs.length < 4) return "Invalid arguments";
                    return String.valueOf(Bukkit.getWorld(blockatargs[3]).getBlockAt(Integer.parseInt(blockatargs[0]), Integer.parseInt(blockatargs[1]), Integer.parseInt(blockatargs[2])).getType());
                case "isblocknatural": //x, y, z, world
                    String[] isBlockNaturalArgs = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    Block isBlockNaturalBlock = Bukkit.getWorld(isBlockNaturalArgs[3]).getBlockAt(Integer.valueOf(isBlockNaturalArgs[0]), Integer.valueOf(isBlockNaturalArgs[1]), Integer.valueOf(isBlockNaturalArgs[2]));
                    return String.valueOf(Utils.isNaturallyGenerated(isBlockNaturalBlock));
                case "weightedrandom":
                    String[] weightedRandomArgs = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);

                    int totalWeight = 0;
                    List<String> items = new ArrayList<>();
                    List<Integer> numbers = new ArrayList<>();
                    for (int i = 0; i < weightedRandomArgs.length; i++) {
                        if (i % 2 == 0) {
                            items.add(weightedRandomArgs[i]);
                        } else {
                            numbers.add(totalWeight + Integer.parseInt(weightedRandomArgs[i]));
                            totalWeight += Integer.parseInt(weightedRandomArgs[i]);
                        }
                    }
                    int randomweightedrandom = ThreadLocalRandom.current().nextInt(1, totalWeight);

                    for (int i = 0; i < numbers.size(); i++) {
                        if (randomweightedrandom <= numbers.get(i)) {
                            return items.get(i);
                        }
                    }
                case "armorset":
                    String[] armorSetArgs = org.apache.commons.lang3.StringUtils.splitByWholeSeparatorPreserveAllTokens(arguments, separator);
                    if (armorSetArgs == null) return "Invalid args.";

                    String armorSetID = armorSetArgs[0];
                    Player p = player.getPlayer();

                    if (p == null || !p.isOnline()) return "false";

                    ItemStack helm = p.getInventory().getHelmet();
                    if (helm == null) return "false";
                    ItemStack chest = p.getInventory().getChestplate();
                    if (chest == null) return "false";
                    ItemStack legs = p.getInventory().getLeggings();
                    if (legs == null) return "false";
                    ItemStack boots = p.getInventory().getBoots();
                    if (boots == null) return "false";
                    if (!helm.hasItemMeta() || !chest.hasItemMeta() || !legs.hasItemMeta() || !boots.hasItemMeta()) return "false";

                    String helmID = helm.getItemMeta().getPersistentDataContainer().get(CommandUtils.keyEIID, PersistentDataType.STRING);
                    if (helmID == null || !helmID.contains(armorSetID + "helm")) return "false";
                    String legsID = legs.getItemMeta().getPersistentDataContainer().get(CommandUtils.keyEIID, PersistentDataType.STRING);
                    if (legsID == null || !legsID.contains(armorSetID + "legs")) return "false";
                    String chestID = chest.getItemMeta().getPersistentDataContainer().get(CommandUtils.keyEIID, PersistentDataType.STRING);
                    if (chestID == null || !chestID.contains(armorSetID + "chest")) return "false";
                    String bootsID = boots.getItemMeta().getPersistentDataContainer().get(CommandUtils.keyEIID, PersistentDataType.STRING);
                    if (bootsID == null || !bootsID.contains(armorSetID + "boots")) return "false";

                    return "true";
                case "worldenvironment":
                    return player.getPlayer().getWorld().getEnvironment().toString();
                case "if":
                    String[] inputSplit = arguments.split(elseIfKeyword);
                    String[] elseSplit = inputSplit[inputSplit.length - 1].split(elseKeyword);

                    String[] combinedSplit = ArrayUtils.addAll(inputSplit, elseSplit);

                    //If and Else If's
                    for (int i = 0; i <= combinedSplit.length; i++) {
                        String[] argSplit = combinedSplit[i].split(conditionSeparator, 2);
                        if (argSplit[1].contains("=")) {
                            String[] condition = argSplit[1].split("=", 1);
                            if (Objects.equals(condition[0], condition[1])) {
                                return argSplit[2];
                            }
                        } else if (argSplit[1].contains("!=")) {
                            String[] condition = argSplit[1].split("!=", 1);
                            if (!Objects.equals(condition[0], condition[1])) {
                                return argSplit[2];
                            }
                        } else if (argSplit[1].contains(">")) {
                            String[] condition = argSplit[1].split(">", 1);
                            if (NumberUtils.isCreatable(condition[0]) && NumberUtils.isCreatable(condition[1]) && (Double.parseDouble(condition[0]) > Double.parseDouble(condition[1]))) {
                                return argSplit[2];
                            }
                        } else if (argSplit[1].contains("<")) {
                            String[] condition = argSplit[1].split("<", 1);
                            if (NumberUtils.isCreatable(condition[0]) && NumberUtils.isCreatable(condition[1]) && (Double.parseDouble(condition[0]) < Double.parseDouble(condition[1]))) {
                                return argSplit[2];
                            }
                        } else if (argSplit[1].contains(">=")) {
                            String[] condition = argSplit[1].split(">=", 1);
                            if (NumberUtils.isCreatable(condition[0]) && NumberUtils.isCreatable(condition[1]) && (Double.parseDouble(condition[0]) >= Double.parseDouble(condition[1]))) {
                                return argSplit[2];
                            }
                        } else if (argSplit[1].contains("<=")) {
                            String[] condition = argSplit[1].split("<=", 1);
                            if (NumberUtils.isCreatable(condition[0]) && NumberUtils.isCreatable(condition[1]) && (Double.parseDouble(condition[0]) <= Double.parseDouble(condition[1]))) {
                                return argSplit[2];
                            }
                        }
                    }

                    //Else
                    return combinedSplit[combinedSplit.length - 1];
                default:
                    separator = function.replace("\\_", "_");
                    String[] temp = arguments.split("_", 2);
                    function = temp[0];
                    arguments = temp[1];
            }
        }


        return super.onRequest(player, args);
    }
}
