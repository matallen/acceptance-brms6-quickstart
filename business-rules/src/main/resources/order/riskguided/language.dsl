[condition][]There is an order=$o:Order($amount:amount,$ctry:country)
[condition][]with amount under {amount}=eval($amount<={amount})
[condition][]which originates in {ctry}=eval($ctry==Country.{ctry})
[consequence][]Set risk status to {status}=$o.setRiskStatus("{status}");
