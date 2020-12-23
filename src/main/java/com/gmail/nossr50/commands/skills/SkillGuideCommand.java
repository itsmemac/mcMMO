package com.gmail.nossr50.commands.skills;

import com.gmail.nossr50.datatypes.skills.CoreSkills;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.util.text.StringUtils;
import com.neetgames.mcmmo.skill.RootSkill;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

//TODO: Switch to root skill based
public class SkillGuideCommand implements CommandExecutor {
    private final String header;
    private final RootSkill rootSkill;
    private final ArrayList<String> guide;

    private final String invalidPage = LocaleLoader.getString("Guides.Page.Invalid");

    public SkillGuideCommand(@NotNull RootSkill rootSkill) {
        skill = CoreSkills.getSkill(rootSkill);
        header = LocaleLoader.getString("Guides.Header", skill.getName());
        guide = getGuide(skill);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        switch (args.length) {
            case 1:
                if (!args[0].equals("?")) {
                    return false;
                }

                sendGuide(sender, 1);
                return true;

            case 2:
                int totalPages = getTotalPageNumber();

                if (!StringUtils.isInt(args[1])) {
                    sender.sendMessage(invalidPage);
                    return true;
                }

                int pageNumber = Integer.parseInt(args[1]);

                if (pageNumber > totalPages || pageNumber <= 0) {
                    sender.sendMessage(LocaleLoader.getString("Guides.Page.OutOfRange", totalPages));
                    return true;
                }

                sendGuide(sender, pageNumber);
                return true;

            default:
                return false;
        }
    }

    private int getTotalPageNumber() {
        return (int) Math.ceil(guide.size() / 8.0);
    }

    private void sendGuide(CommandSender sender, int pageNumber) {
        for (String target : grabPageContents(pageNumber)) {
            sender.sendMessage(target);
        }
    }

    private ArrayList<String> grabPageContents(int page) {
        int pageIndexStart = 8 * (page - 1); // Determine what string to start at
        ArrayList<String> allStrings = new ArrayList<>();

        allStrings.add(header);

        // Add targeted strings
        while (allStrings.size() < 9) {
            if (pageIndexStart + allStrings.size() > guide.size()) {
                allStrings.add("");
            }
            else {
                allStrings.add(guide.get(pageIndexStart + (allStrings.size() - 1)));
            }
        }

        allStrings.add("Page " + page + " of " + getTotalPageNumber());
        return allStrings;
    }

    private ArrayList<String> getGuide(RootSkill rootSkill) {
        ArrayList<String> guide = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String[] section = LocaleLoader.getString("Guides." + StringUtils.getCapitalized(skill.toString()) + ".Section." + i).split("\n");

            if (section[0].startsWith("!")) {
                break;
            }

            guide.addAll(Arrays.asList(section));

            if (section.length < 8) {
                for (int blankLine = 8 - section.length; blankLine > 0; blankLine--) {
                    guide.add("");
                }

            }
        }

        return guide;
    }
}
