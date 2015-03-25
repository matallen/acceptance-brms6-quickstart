[condition][]There is an order=$o:Order($amount:amount,$ctry:country)
[condition][]with amount under {amount}=eval($amount<={amount})
[condition][]that originates from {ctry_list}=eval("{ctry_list}".contains($ctry.name()))
[consequence][]Set risk check eligibility to {eligibility}=$o.setRiskCheck({eligibility}); System.out.println("Setting RiskCheck = true for order"+$o);
[consequence][]ensure order is risk checked=$o.setRiskCheck(true); System.out.println("Setting RiskCheck = true for order"+$o);