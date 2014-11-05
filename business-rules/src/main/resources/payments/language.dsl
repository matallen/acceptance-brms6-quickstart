[condition][]There is an account=$a:Account($balance:balance, $overdraft:overdraft)
[condition][]There is a payment scheduled=$p:Payment(sent==false, fromAccount==$a.accountId)
[condition][]There is enough funds in the account=eval($p.getValue()<=$balance)
[condition][]There is enough funds including the overdraft in the account=eval($p.getValue()<=($balance+$overdraft))
[condition][]There is only enough fund when using the overdraft on the account=eval($p.getValue()>$balance && $p.getValue()<=($balance+$overdraft))
[condition][]There is not enough funds including the overdraft=eval($p.getValue()>($balance+$overdraft))
[consequence][]Payment will be sent=$p.setSent(true);
[consequence][]Owner will be notified of successful payment=$p.notify($a.getAccountId(), "SUCCESS");
[consequence][]Payment will not be sent=$p.setSent(false);
[consequence][]Owner will be notified of failed payment=$p.notify($a.getAccountId(), "FAILURE");
