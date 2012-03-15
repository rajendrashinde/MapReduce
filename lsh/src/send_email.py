import smtplib
from email.mime.text import MIMEText
fp = open('temp.txt', 'r')
msg = MIMEText(fp.read())
fp.close()

job_id = '1'
msg['Subject'] = 'Job_id %s' % job_id
msg['From'] = me
msg['To'] = you

s = smtplib.SMTP('localhost')

