[condition][]There is an account=$a:Account($balance:balance, $overdraft:overdraft)
[condition][]There is a payment scheduled=$p:Payment(fromAccount==$a.accountId)
[condition][]There is enough funds in the account=eval($p.getValue()<=$balance)
[condition][]There is enough funds including the overdraft in the account=eval($p.getValue()<=($balance+$overdraft))
[condition][]There is only enough fund when using the overdraft on the account=eval($p.getValue()>$balance && $p.getValue()<=($balance+$overdraft))
[consequence][]Payment will be sent=$p.setSent(true);
[consequence][]Owner will be notified of succesful payment=$p.notify($a.getAccountId(), "SUCCESS");
