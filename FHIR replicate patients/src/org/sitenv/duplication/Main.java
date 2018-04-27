package org.sitenv.duplication;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main 
{
	static Statement stmt  	 = null;
	static Connection conn 	 = null;
	static ResultSet r1      = null;
	static ResultSet r2 	 = null;
	static ResultSet r3 	 = null;
	static ResultSet r4		 = null;
	static ResultSet r5		 = null;
	static PreparedStatement pstmt = null;
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();

	static Scanner scanner = new Scanner(System.in);
	public static Integer scanI(String prompt) 
	{
		System.out.println(prompt);
		Integer x = scanner.nextInt();
		return x;
	}

	public static Integer maxval()
	{
		Integer max_id=0;
		try {

			String q7 = "SELECT MAX(id) FROM patient_json;";
			r5 = stmt.executeQuery(q7);
			while(r5.next())
			{
				max_id = r5.getInt(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return max_id;
	}
	
	

	public static String randomString( int len ){
	   StringBuilder sb = new StringBuilder( len );
	   for( int i = 0; i < len; i++ ) 
	      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
	   return sb.toString();
	}

	public static void main(String[] args) 
	{
		//scans db names

		System.out.println("Enter the database Name :");
		String dbname = scanner.next();

		System.out.println("Entered database Name is : "+dbname);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}


		String jdbcUrl = "jdbc:postgresql://localhost:5432/"+dbname;
		String username = "postgres";
		String password = "postgres"; 

		String[] s1= new String[4];
		String[] s2= {"id","identifier", "full_name", "telecom", "gender", "birth_date","marital_status", "communication_language", "active","race", "ethnicity", "religion","mothers_maiden_name","address_line1","address_line2","address_city","address_state","address_zip", "address_country","family_name","given_name","birth_place","birthsex","last_updated"};
		String[] s3={"id","identifier_system","identifier_value","name"};
		String[] s5;

		Integer max_id=0;
		String table_name = "patient";
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		try 
		{
			/* Step 1 - Load driver*/ 		
			Class.forName("org.postgresql.Driver"); 

			/* Step 2 - Open connection*/ 	
			conn = DriverManager.getConnection(jdbcUrl, username, password);

			/* Step 3 - Execute statement*/ 
			stmt = conn.createStatement();

			//			-----------------------------------------------------	COLUMN NAMES	of	Patient_json -----------------------------------------------------

			String table_2 = "patient_json";

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			//			-----------------------------------------------------   NO OF REPETETIONS



			max_id = maxval();

			System.out.println("THE NUMBER OF RECORDS PRESENT IN \"PATIENT\" : "+max_id);

			Integer n2o = scanI("Enter the Number of records, Patient resource should have : ");
			s5 = new String[24];
			
			Integer rep = n2o-max_id;
			//			-----------------------------------------------------	INSERTING
			for(int no = 0;no<=rep ;no++)
			{
				//																FINDING THE MAX(id)
				max_id = maxval();

				for(int q = 1;q<=max_id;q++)	//ROWS
				{
					String random = randomString(2);
					
					max_id = maxval();

					if(max_id >= n2o)
					{
						System.out.println("Process has been completed...!");
						System.out.println("The total number of Patient records : "+max_id);
						System.exit(0);
					}

					//----------------------------------------------QUERY PATIENT-------------------------------------------------------------

					String q2 = "SELECT * FROM patient WHERE id="+q;		// QUERY FOR PATIENT

					//EXTRACT RESULT SET FOR PATIENT
					r2 = stmt.executeQuery(q2);									

					while(r2.next())
					{
						s1[0]=""+r2.getInt(s3[0]);
						s1[1]=r2.getString(s3[1]);
						s1[2]=r2.getString(s3[2]);
						s1[3]=r2.getString(s3[3]);
					}//end while 2
					s1[3] = s1[3]+random;
					String q4 = "INSERT INTO "+table_name+"( identifier_system, identifier_value, name)"
							+" VALUES (?,(SELECT MAX(identifier_value::integer) FROM patient WHERE identifier_value ~ E'^\\\\d+$')+1,?);";


					//EXECUTE PREPARED STATEMENT
					pstmt = conn.prepareStatement(q4);
					//SET STRING FOR ONLY P1, P2 NOT REQUIRED
					pstmt.setString(1,s1[1]);
					pstmt.setString(2,s1[3]);
					int result = pstmt.executeUpdate();

					//----------------------------------------------QUERY PATIENT_JSON-------------------------------------------------------------


					String q3 = "SELECT * FROM patient_json WHERE id="+q;		//QUERY FOR PATIENT_JSON
					//EXECUTE QUERY
					r4 = stmt.executeQuery(q3);
					//EXTRACT RESULTSET FOR PATIENT_JSON
					while(r4.next())
					{
						s5[0]=""+r4.getInt(s2[0]);
						for (int i = 1; i <= 23; i++) 
						{
							s5[i]=r4.getString(s2[i]);	
						}
					}

					System.out.println(".");

					s5[2] = s5[2]+random;
					s5[20] = s5[20]+random;
					String q5 ="INSERT INTO "+table_2+" ( identifier, full_name, telecom, gender, birth_date, marital_status, communication_language, active, race, ethnicity, religion, mothers_maiden_name, address_line1, address_line2, address_city, address_state, address_zip, address_country, family_name, given_name, birth_place, birthsex, last_updated)"
							+" VALUES('"
							+s5[1]+"','"+s5[2]+"','"+s5[3]+"','"+s5[4]+"','"+s5[5]+"','"+s5[6]+"','"+s5[7]+"','"+s5[8]+"','"+s5[9]+"','"+s5[10]+"','"+s5[11]+"','"+s5[12]+"','"+s5[13]+"','"+s5[14]+"','"+s5[15]+"','"+s5[16]+"','"+s5[17]+"','"+s5[18]+"','"+s5[19]+"','"+s5[20]+"','"+s5[21]+"','"+s5[22]+"','"+s5[23]+"');";

					stmt = conn.createStatement();
					stmt.executeUpdate(q5);
				}//end for 2
			}
		}// end try


		catch (SQLException e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
		} 
		finally 
		{
			try 
			{
				// Step 5 Close connection
				if (stmt != null) 
				{
					stmt.close();
				}
				if (r1 != null) 
				{
					r1.close();
				}
				if (r2 != null) 
				{
					r2.close();
				}
				if (r3 != null) 
				{
					r3.close();
				}
				if (r4 != null) 
				{
					r4.close();
				}

				if (conn != null) 
				{
					conn.close();
				}
				if(pstmt !=null)
				{
					pstmt.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}

	}
}



