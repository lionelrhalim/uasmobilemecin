package umn.ac.mecinan.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import umn.ac.mecinan.listener.OnGetUserInProjectListener;
import umn.ac.mecinan.model.ButtonProject;
import umn.ac.mecinan.model.Project;
import umn.ac.mecinan.adapter.ProjectsViewAdapter;
import umn.ac.mecinan.R;
import umn.ac.mecinan.model.ProjectAttributes;
import umn.ac.mecinan.model.User;
import umn.ac.mecinan.listener.OnGetProjectDataListener;
import umn.ac.mecinan.listener.OnGetUserProjectRoleListener;

public class MainOngoingFragment extends Fragment {

    /**
     * DECLARATION - FIREBASE
     */
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser curr_user = auth.getCurrentUser();

    /**
     * DECLARATION - DATABASE PROJECT
     */
    private String title;
    private Date dateStart, dateDue;
    private String idEmployee, idClient;
    private String idField, idCategory;
    private String desc;
    private int price;
    private int status;
    private Date dateEnd;
    private float rating;

    public MainOngoingFragment() {
        // Required empty public constructor
        Log.d("EZRA", "Start = MainOngoingFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("EZRA", "onCreateView MainOngoingFragment");
        final View myFragmentView = inflater.inflate(R.layout.fragment_main_ongoing, container, false);

        /**
         * FRAGMENT ONGOING
         */


        /**
         * Checking Attached Activity
         */
        Activity activity = getActivity();

        if(isAdded() && activity != null) {
            /**
             * Retrieve Ongoing Project
             */
            Project project = new Project();
            project.retrieveProject(new OnGetProjectDataListener() {
                final String TAG = "retrieve_ongoingProject";
                ProjectsViewAdapter ongoingAdapter;

                List<Project> allProject = new ArrayList<>();
                List<ProjectAttributes> projectAttributes = new ArrayList<>();

                Boolean firstInit = true;

                @Override
                public void onStart() {
                    TextView tvEmpty;
                    ProgressBar pBar;
                    tvEmpty = myFragmentView.findViewById(R.id.tvEmptyOngoingProject);
                    pBar = myFragmentView.findViewById(R.id.pBar);

                    tvEmpty.setText(getString(R.string.loading_ongoing_project));
                    pBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onDataChange(Project project) {
                    allProject.add(project);
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "callback success retrieve ongoing project");

                    final String TAG = "user_in_project";
                    Log.d("user_in_project", "retrieving user in project");
                    User userInProject = new User();

                    /**
                     * Joining Process--
                     * Retrieve User who are on the project
                     * (from idEmployee and idClient)
                     */
                    userInProject.retrieveUserInProject(curr_user, allProject, new OnGetUserInProjectListener() {
                        TextView tvEmpty = myFragmentView.findViewById(R.id.tvEmptyOngoingProject);
                        RecyclerView recyclerView = myFragmentView.findViewById(R.id.ongoingRecyclerView);
                        ProgressBar pBar = myFragmentView.findViewById(R.id.pBar);

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onDataChange(Project project) {
                            String curr_user_email = curr_user.getEmail();
                            User employee = project.getUserEmployee();

                            if(curr_user_email.equals(employee.getEmail())) {
                                tvEmpty.setVisibility(View.GONE);
                                pBar.setVisibility(View.GONE);

                                if(project.getStatus() == 2 || project.getStatus() == 0) {
                                    ButtonProject buttonProject = new ButtonProject();

                                    /** Transferring to Project Attributs **/
                                    projectAttributes.add(
                                            new ProjectAttributes(
                                                    project,
                                                    true,
                                                    buttonProject.makeButton(project.getStatus())
                                            )
                                    );
                                } else {
                                    ButtonProject buttonProject = new ButtonProject();

                                    /** Transferring to Project Attributs **/
                                    projectAttributes.add(
                                            new ProjectAttributes(
                                                    project,
                                                    true,
                                                    buttonProject.makeButton(4)
                                            )
                                    );
                                }
                            }
                        }

                        @Override
                        public void onSuccess() {

                            /** Sorting Ascending to deadline **/
                            Collections.sort(projectAttributes, new Comparator<ProjectAttributes>() {
                                @Override
                                public int compare(ProjectAttributes o1, ProjectAttributes o2) {
                                    return Long.compare(o1.getProject().getDate(), o2.getProject().getDate());
                                }
                            });

                            List<ProjectAttributes> projectAttributesUpdate = new ArrayList<>(projectAttributes);

                            /**
                             * Set to recycler view from listongoing
                             */
                            if(firstInit) {
                                ongoingAdapter = new ProjectsViewAdapter(getActivity(), projectAttributesUpdate);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerView.setAdapter(ongoingAdapter);

                                projectAttributes.clear();

                                firstInit = false;
                            } else {
                                ongoingAdapter.updateProjectList(projectAttributesUpdate);

                                projectAttributes.clear();
                            }

                            pBar.setVisibility(View.GONE);

                            if(ongoingAdapter.getItemCount() <= 0) {
                                tvEmpty.setText(getString(R.string.empty_ongoing_project));
                                tvEmpty.setVisibility(View.VISIBLE);
                            }

                            allProject.clear();
                        }

                        @Override
                        public void onFailed(DatabaseError databaseError) {
                            Log.d(TAG, "dbError: " + databaseError);
                        }
                    });
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.d(TAG, "dbError: " + databaseError);
                }
            });
        }

        return myFragmentView;
    }
}
