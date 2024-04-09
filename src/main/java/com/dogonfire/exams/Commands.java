package com.dogonfire.exams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class Commands
{
    static private Commands instance;

    public Commands()
    {
        instance = this;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player player = null;

        if ((sender instanceof Player))
        {
            player = (Player) sender;
        }

        if (player == null)
        {
            if ((cmd.getName().equalsIgnoreCase("exams")) || (cmd.getName().equalsIgnoreCase("exam")))
            {
                if (args.length == 1)
                {
                    if (args[0].equalsIgnoreCase("reload"))
                    {
                        Exams.instance().reloadSettings();

                        return true;
                    } else if (args[0].equalsIgnoreCase("clean"))
                    {
                        commandClean(sender);

                        return true;
                    }
                }

                commandExamList(player);
            }

            return true;
        }

        if ((cmd.getName().equalsIgnoreCase("exams")) || (cmd.getName().equalsIgnoreCase("exam")))
        {
            if (args.length == 0)
            {
                commandPluginInfo(sender);
                return true;
            }
            if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("reload"))
                {
                    if ((!player.isOp()) && (!player.hasPermission("exams.reload")))
                    {
                        return false;
                    }

                    Exams.instance().reloadSettings();
                    sender.sendMessage(ChatColor.YELLOW + Exams.instance().getDescription().getFullName() + ":" + ChatColor.AQUA + " Reloaded configuration.");
                    return true;
                }
                if (args[0].equalsIgnoreCase("help"))
                {
                    if ((!player.isOp()) && (!player.hasPermission("exams.help")))
                    {
                        return false;
                    }

                    commandHelp(sender);

                    return true;
                }
                if (args[0].equalsIgnoreCase("clean"))
                {
                    if (!player.isOp() && !player.hasPermission("exams.clean"))
                    {
                        return false;
                    }

                    commandClean(sender);
                    return true;
                }
                if ((args[0].equalsIgnoreCase("a")) || (args[0].equalsIgnoreCase("b")) || (args[0].equalsIgnoreCase("c")) || (args[0].equalsIgnoreCase("d")))
                {
                    commandAnswer(player, args[0].toLowerCase());
                } else
                {
                    if (args[0].equalsIgnoreCase("list"))
                    {
                        if ((!player.isOp()) && (!player.hasPermission("exams.list")))
                        {
                            return false;
                        }

                        commandList(sender);
                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "Invalid Exams command! Try /exams help");
                    return true;
                }
            } else
            {
                if (args.length == 2)
                {
                    if (args[0].equalsIgnoreCase("info"))
                    {
                        if (!player.isOp() && !player.hasPermission("exams.info"))
                        {
                            return false;
                        }

                        commandInfo(sender, args[1]);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("reset"))
                    {
                        if (!player.isOp() && !player.hasPermission("exams.reset"))
                        {
                            return false;
                        }

                        commandReset(sender, args[1]);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("test"))
                    {
                        if (!player.isOp() && !player.hasPermission("exams.test"))
                        {
                            return false;
                        }

                        commandTest(sender, args[1]);
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("studentinfo"))
                    {
                        if (!player.isOp() && !player.hasPermission("exams.studentinfo"))
                        {
                            return false;
                        }

                        commandStudentInfo(sender, args[1]);
                        return true;
                    }

                    sender.sendMessage(ChatColor.RED + "Invalid Exams command! Try /exams help");
                    return true;
                }

                if (args.length > 3)
                {
                    sender.sendMessage(ChatColor.RED + "Too many arguments! Check /exams help");
                    return true;
                }
            }
        }
        return true;
    }

    private boolean commandInfo(CommandSender sender, String examName)
    {
        return true;
    }

    private boolean commandList(CommandSender sender)
    {
        sender.sendMessage(ChatColor.AQUA + "Exams in " + Exams.instance().serverName + ":");

        // Checks for exam
        List<String> exams = ExamManager.getExams();
        StringBuilder examsString = new StringBuilder();
        int i = 0;
        for (String examName : exams)
        {
            i += 1;
            if (i == 1)
            {
                examsString.append((String) (ChatColor.DARK_AQUA + examName));
            } else
            {
                examsString.append((String) (ChatColor.AQUA + ", " + ChatColor.DARK_AQUA + examName));
            }
        }
        sender.sendMessage(examsString.toString());

        return true;
    }

    private boolean commandReset(CommandSender sender, String playerName)
    {
        StudentManager.removeStudent(playerName);
        StudentManager.resetExamTime(playerName);

        sender.sendMessage(ChatColor.YELLOW + Exams.instance().getDescription().getFullName() + ":" + ChatColor.AQUA + " Reset of player " + ChatColor.YELLOW + playerName + ChatColor.AQUA + "'s studentdata was successful!");

        return true;
    }

    private boolean commandStudentInfo(CommandSender sender, String playerName)
    {
        sender.sendMessage(ChatColor.YELLOW + "Student data for: " + playerName);

        // Checks for exam
        String currentExam = StudentManager.getExamForStudent(playerName);
        if (currentExam != null)
        {
            sender.sendMessage(ChatColor.AQUA + "In exam?" + ChatColor.WHITE + " - Yes");
            sender.sendMessage(ChatColor.AQUA + "Exam name:" + ChatColor.WHITE + " - " + currentExam);

            String examTime = StudentManager.getLastExamTime(playerName);
            if (examTime != null)
            {
                sender.sendMessage(ChatColor.AQUA + "Last exam time:" + ChatColor.WHITE + " - " + examTime);
            }
            List<String> passedExams = StudentManager.getPassedExams(playerName);
            String passedExamsByComma = String.join(", ", passedExams);
            if (passedExams != null)
            {
                sender.sendMessage(ChatColor.AQUA + "Passed exams:" + ChatColor.WHITE + " - " + passedExamsByComma);
            }
        } else
        {
            String examTime = StudentManager.getLastExamTime(playerName);
            if (examTime != null)
            {
                sender.sendMessage(ChatColor.AQUA + "In exam?" + ChatColor.WHITE + " - No");
                sender.sendMessage(ChatColor.AQUA + "Last exam time:" + ChatColor.WHITE + " - " + examTime);
                List<String> passedExams = StudentManager.getPassedExams(playerName);
                String passedExamsByComma = String.join(", ", passedExams);
                if (passedExams != null)
                {
                    sender.sendMessage(ChatColor.AQUA + "Passed exams:" + ChatColor.WHITE + " - " + passedExamsByComma);
                }
            } else
            {
                List<String> passedExams = StudentManager.getPassedExams(playerName);
                String passedExamsByComma = String.join(", ", passedExams);
                if (passedExams != null)
                {
                    sender.sendMessage(ChatColor.AQUA + "In exam?" + ChatColor.WHITE + " - No");
                    sender.sendMessage(ChatColor.AQUA + "Passed exams:" + ChatColor.WHITE + " - " + passedExamsByComma);
                } else
                {
                    sender.sendMessage(ChatColor.AQUA + "No student data found for player:" + ChatColor.WHITE + " - " + playerName);
                }
            }
        }


        return true;
    }

    private boolean commandTest(CommandSender sender, String exam)
    {
        Player player = (Player) sender;

        String examName = exam;

        if (!ExamManager.examExists(examName))
        {
            player.sendMessage(ChatColor.RED + "There is no exam called '" + examName + "'!");
            return false;
        }

        String currentExam = StudentManager.getExamForStudent(player.getName());

        if (currentExam == null)
        {
            ExamManager.handleNewExamPrerequisites(player, examName);

            return false;
        }

        if (!currentExam.equals(examName))
        {
            Exams.sendInfo(player, ChatColor.RED + "You are already signed up for the " + ChatColor.YELLOW + currentExam + ChatColor.RED + " exam!");
            return false;
        }

        if (ExamManager.isExamOpen(player.getWorld(), examName))
        {
            if (!StudentManager.isDoingExam(player.getName()))
            {
                if (!ExamManager.generateExam(player.getName(), examName))
                {
                    player.sendMessage(ChatColor.RED + "ERROR: Could not generate a " + ChatColor.YELLOW + examName + ChatColor.RED + "exam!");
                    return false;
                }

                Exams.sendToAll(ChatColor.AQUA + player.getName() + " started on the exam for " + ChatColor.YELLOW + examName + ChatColor.AQUA + "!");
                Exams.sendMessage(player.getName(), "You started on the " + ChatColor.YELLOW + examName + ChatColor.AQUA + " exam.");
                Exams.sendMessage(player.getName(), "Click on the sign again to repeat the exam question.");
                Exams.sendMessage(player.getName(), "Good luck!");

                ExamManager.nextExamQuestion(player.getName());
            }

            ExamManager.doExamQuestion(player.getName());
        }

        return true;
    }

    private void commandAnswer(Player player, String answer)
    {
        if (!StudentManager.isDoingExam(player.getName()) || StudentManager.getExamForStudent(player.getName()) == null)
        {
            player.sendMessage(ChatColor.RED + "You are not taking any exam!");
            return;
        }

        StudentManager.answer(player.getName(), answer);

        if (ExamManager.nextExamQuestion(player.getName()))
        {
            ExamManager.doExamQuestion(player.getName());
        } else
        {
            ExamManager.calculateExamResult(player.getName());
            StudentManager.removeStudent(player.getName());
        }
    }

    private boolean commandPluginInfo(CommandSender sender)
    {
        sender.sendMessage(ChatColor.YELLOW + "---------------- " + Exams.instance().getDescription().getFullName() + " ----------------");
        sender.sendMessage(ChatColor.AQUA + "By " + Exams.instance().getDescription().getAuthors());
        sender.sendMessage(ChatColor.AQUA + "");
        sender.sendMessage(ChatColor.AQUA + "There are currently " + ChatColor.WHITE + ExamManager.getExams().size() + ChatColor.AQUA + " exams in " + Exams.instance().serverName);
        sender.sendMessage(ChatColor.AQUA + "");
        sender.sendMessage(ChatColor.AQUA + "Use " + ChatColor.WHITE + "/exams help" + ChatColor.AQUA + " for a list of commands");

        return true;
    }

    private boolean commandHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.YELLOW + "---------------- " + Exams.instance().getDescription().getFullName() + " ----------------");
        sender.sendMessage(ChatColor.AQUA + "/exams" + ChatColor.WHITE + " - Basic info");
        //sender.sendMessage(ChatColor.AQUA + "/exams list" + ChatColor.WHITE + " - List of all exams");
        sender.sendMessage(ChatColor.AQUA + "/exams a" + ChatColor.WHITE + " - Answer A to an exam question");
        sender.sendMessage(ChatColor.AQUA + "/exams b" + ChatColor.WHITE + " - Answer B to an exam question");
        sender.sendMessage(ChatColor.AQUA + "/exams c" + ChatColor.WHITE + " - Answer C to an exam question");
        sender.sendMessage(ChatColor.AQUA + "/exams d" + ChatColor.WHITE + " - Answer D to an exam question");
        if ((sender.isOp()) || (sender.hasPermission("exams.reload")))
        {
            sender.sendMessage(ChatColor.AQUA + "/exams reload" + ChatColor.WHITE + " - Reloads the Exams system");
        }
        if ((sender.isOp()) || (sender.hasPermission("exams.clean")))
        {
            sender.sendMessage(ChatColor.AQUA + "/exams clean" + ChatColor.WHITE + " - Cleans up expired student data");
        }
        if ((sender.isOp()) || (sender.hasPermission("exams.reset")))
        {
            sender.sendMessage(ChatColor.AQUA + "/exams reset <player>" + ChatColor.WHITE + " - Resets student data for a player");
        }
        if ((sender.isOp()) || (sender.hasPermission("exams.test")))
        {
            sender.sendMessage(ChatColor.AQUA + "/exams test <exam>" + ChatColor.WHITE + " - Validates an exam");
        }
        if ((sender.isOp()) || (sender.hasPermission("exams.studentinfo")))
        {
            sender.sendMessage(ChatColor.AQUA + "/exams studentinfo <player>" + ChatColor.WHITE + " - Gets info about a student");
        }

        return true;
    }

    private boolean commandClean(CommandSender sender)
    {
        int students = 0;

        students = ExamManager.cleanStudentData();

        if (sender != null)
        {
            sender.sendMessage(ChatColor.YELLOW + Exams.instance().getDescription().getFullName() + ":" + ChatColor.AQUA + " Cleaned up data for " + ChatColor.YELLOW + students + ChatColor.AQUA + " students");
        }

        Exams.log("Cleaned up data for " + students + " students");

        return true;
    }

    private void commandExamList(CommandSender sender)
    {
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> result = new ArrayList<String>();

        Player player = null;
        if (sender instanceof Player)
        {
            player = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("exams") || cmd.getName().equalsIgnoreCase("exam"))
        {
            if (args.length == 1)
            {
                List<String> arg1 = new ArrayList<String>();
                arg1.add("help");
                if (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.reload"))
                {
                    arg1.add("reload");
                }
                if (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.clean"))
                {
                    arg1.add("clean");
                }
                if (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.list"))
                {
                    arg1.add("list");
                }
                if (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.reset"))
                {
                    arg1.add("reset");
                }
                if (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.studentinfo"))
                {
                    arg1.add("studentinfo");
                }
                if (player != null)
                {
                    if (StudentManager.isDoingExam(player.getName()) && StudentManager.getExamForStudent(player.getName()) != null)
                    {
                        arg1.add("a");
                        arg1.add("b");
                        arg1.add("c");
                        arg1.add("d");
                    }
                    if (player.isOp() || PermissionsManager.hasPermission(player, "exams.test"))
                    {
                        arg1.add("test");
                    }
                }
                Iterable<String> FIRST_ARGUMENTS = arg1;
                StringUtil.copyPartialMatches(args[0], FIRST_ARGUMENTS, result);
            } else if (args.length == 2)
            {
                if (args[0].equalsIgnoreCase("reset") && (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.reset")))
                {
                    return null;
                } else if (args[0].equalsIgnoreCase("studentinfo") && (player == null || player.isOp() || PermissionsManager.hasPermission(player, "exams.studentinfo")))
                {
                    return null;
                } else
                {
//					List<String> arg2 = new ArrayList<String>();
//
//					arg2.add("2500");
//					arg2.add("5000");
//
//					Iterable<String> SECOND_ARGUMENTS = arg2;
//					StringUtil.copyPartialMatches(args[1], SECOND_ARGUMENTS, result);
                }
            }
        }

        Collections.sort(result);
        return result;
    }
}